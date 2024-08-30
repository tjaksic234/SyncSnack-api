package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.NotValidEnumException;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.OrderStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.OrderRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.EventService;
import com.example.KavaSpring.services.FirebaseMessagingService;
import com.example.KavaSpring.services.WebSocketService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserProfileRepository userProfileRepository;

    private final ConverterService converterService;

    private final MongoTemplate mongoTemplate;

    private final WebSocketService webSocketService;

    private final OrderRepository orderRepository;

    private final FirebaseMessagingService firebaseMessagingService;

    private final GroupRepository groupRepository;


    @Override
    public EventResponse createEvent(String groupId, EventRequest request) {
        List<EventStatus> activeStatuses = Arrays.asList(EventStatus.PENDING, EventStatus.IN_PROGRESS);
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("Group not found"));

        if (userProfile == null) {
            throw new NotFoundException("No UserProfile associated with the id");
        }

        if (eventRepository.existsByUserProfileIdAndStatusIn(userProfile.getId(), activeStatuses)) {
            throw new IllegalStateException("User already has an active event (PENDING or IN_PROGRESS)");
        }


        Event event = new Event();
        event.setUserProfileId(userProfile.getId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setGroupId(groupId);
        event.setEventType(request.getEventType());
        event.setPendingUntil(LocalDateTime.now().plusMinutes(request.getPendingTime()));
        eventRepository.save(event);

        //? notifying the group members through websocket
        webSocketService.notifyGroupMembers(event);

        //? notifying the group of the created event on mobile through firebase
        try {
            firebaseMessagingService.notifyGroupOfNewEvent(event);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }

        log.info("Event created");
        return converterService.convertToEventResponse(request);
    }

    @Override
    public EventExpandedResponse getEventById(String id) {
        Event event = eventRepository.getById(id).orElseThrow(() -> new NotFoundException("No event associated with the id"));
        log.info("Get event by id finished");
        return converterService.convertToEventExpandedResponse(event);
    }



    @Override
    public List<EventExpandedResponse> filterEvents(String groupId, EventSearchRequest request) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("Group not found"));
        List<Criteria> criteriaList = new ArrayList<>();

        if (request.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getEventType() != null && request.getEventType() != EventType.ALL) {
            criteriaList.add(Criteria.where("eventType").is(request.getEventType()));
        }

        criteriaList.add(Criteria.where("groupId").is(groupId));
        criteriaList.add(Criteria.where("userProfileId").ne(userProfile.getId()));

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        MatchOperation matchOperation = Aggregation.match(combinedCriteria);

        ProjectionOperation projectOperation = Aggregation.project()
                .and("eventId").as("eventId")
                .and("userProfileId").as("userProfileId")
                .and("title").as("title")
                .and("description").as("description")
                .and("groupId").as("groupId")
                .and("status").as("status")
                .and("eventType").as("eventType")
                .and("createdAt").as("createdAt")
                .and("pendingUntil").as("pendingUntil");

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectOperation,
                sortOperation
        );

        AggregationResults<Event> results = mongoTemplate.aggregate(aggregation, "events", Event.class);

        return results
                .getMappedResults()
                .stream()
                .map(converterService::convertToEventExpandedResponse)
                .collect(Collectors.toList());
    }

    //? Cron expression: sec min hrs day mon weekday
    @Scheduled(cron = "* * 1 * * * ")
    @Override
    public void updateEventsJob() {
        LocalDateTime now = LocalDateTime.now();
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where("status").is(EventStatus.PENDING));
        criteriaList.add(Criteria.where("pendingUntil").lt(now));

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));


        Update update = new Update().set("status", EventStatus.IN_PROGRESS);

        mongoTemplate.updateMulti(new Query(combinedCriteria), update, Event.class);


         log.info("Successfully updated the status of events at time ---> {}", LocalDateTime.now(ZoneId.of("Europe/Zagreb")));

    }

    @Override
    public String updateEventStatus(String id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found for ID: " + id));
        event.setStatus(status);
        eventRepository.save(event);
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toString());
        } catch (IllegalArgumentException e) {
            throw new NotValidEnumException("Bad enum value provided from event status");
        }

        List<Order> orders = orderRepository.findAllByEventId(id);
        long updatedOrdersCount = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                order.setStatus(orderStatus);
                orderRepository.save(order);
                updatedOrdersCount++;
            }
        }

        log.info("Event status updated successfully");
        log.info("Updated {} orders for event: {} to status: {}", updatedOrdersCount, id, orderStatus);
        return "Event status updated successfully";
    }

    @Override
    public EventDto getActiveEvent() {
        List<EventStatus> statuses = new ArrayList<>();
        statuses.add(EventStatus.PENDING);
        statuses.add(EventStatus.IN_PROGRESS);

        Event event = eventRepository.getEventByUserProfileIdAndStatusIn(Helper.getLoggedInUserProfileId(), statuses);

        if (event == null) {
            throw new NotFoundException("No active event found");
        }

        log.info("The retrieved active event: {}", event);
        return converterService.convertToEventDto(event);
    }


}
