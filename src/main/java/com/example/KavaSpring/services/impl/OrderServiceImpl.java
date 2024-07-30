package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.OrderRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserProfileRepository userProfileRepository;

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    private final MongoTemplate mongoTemplate;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        //TODO dodaj check da user nemoze napraviti vise od 1 order za event,
        // samo usporedi kad radi order postoji li vec order vezan za taj event

        boolean existsUserProfile = userProfileRepository.existsById(request.getUserProfileId());
        boolean existsEvent = eventRepository.existsById(request.getEventId());


        if (!existsUserProfile) {
            throw new NotFoundException("No userProfile associated with id");
        }

        if (!existsEvent) {
            throw new NotFoundException("No event associated with eventId in the order");
        }

        log.info("the order request is: {}", request);
        Order order = new Order();
        order.setUserProfileId(request.getUserProfileId());
        order.setEventId(request.getEventId());
        order.setAdditionalOptions(request.getAdditionalOptions());
        orderRepository.save(order);

        log.info("Order created");
        return converterService.convertToOrderResponse(request);
    }

    @Override
    public OrderDto getOrderById(String id) {
        Order order = orderRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("No order associated with id"));

        log.info("Get order by id finished");
        return converterService.convertToOrderDto(order);
    }

    @Override
    public List<OrderEventInfoDto> getAllOrdersFromUserProfile() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        if (userProfile == null) {
            throw new NotFoundException("Bad user profile id provided");
        }

        MatchOperation matchOperation = Aggregation.match(Criteria.where("userProfileId").is(userProfile.getId()));

        AddFieldsOperation convertEventIdToObjectId  = Aggregation.addFields()
                .addField("eventId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$eventId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("events", "eventId", "_id", "eventDetails");

        UnwindOperation unwindOperation = Aggregation.unwind("eventDetails");

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("eventId").as("eventId")
                .and("eventDetails.eventType").as("eventType")
                .and("status").as("status")
                .and("additionalOptions").as("additionalOptions")
                .and("rating").as("rating")
                .and("createdAt").as("createdAt");

        SortOperation sortOperation = new SortOperation(Sort.by(Sort.Direction.DESC, "createdAt"));

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                convertEventIdToObjectId,
                lookupOperation,
                unwindOperation,
                projectionOperation,
                sortOperation
        );

        AggregationResults<OrderEventInfoDto> results = mongoTemplate.aggregate(aggregation, "orders", OrderEventInfoDto.class);

        return results
                .getMappedResults()
                .stream()
                .map(converterService::convertToOrderEventInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderActivityResponse> getOrdersByActivityStatus(boolean isActive) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        if (userProfile == null) {
            throw new NotFoundException("User profile is null");
        }

        if (userProfile.getId() == null) {
            throw new IllegalStateException("User profile id is null");
        }

        MatchOperation matchUserOrders  = Aggregation.match(Criteria.where("userProfileId").is(userProfile.getId()));


        AddFieldsOperation convertEventIdToObjectId  = Aggregation.addFields()
                .addField("eventId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$eventId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("events", "eventId", "_id", "event");

        UnwindOperation unwindOperation = Aggregation.unwind("event");

        //! Determine which statuses to include based on isActive parameter
        List<EventStatus> statusesToInclude = isActive
                ? Arrays.asList(EventStatus.PENDING, EventStatus.IN_PROGRESS)
                : Collections.singletonList(EventStatus.COMPLETED);

        MatchOperation matchEventStatus  = Aggregation.match(Criteria.where("event.status").in(statusesToInclude));

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("event._id").as("eventId")
                .and("_id").as("orderId")
                .and("event.creatorId").as("creatorId")
                .and("event.title").as("title")
                .and("event.description").as("description")
                .and("event.groupId").as("groupId")
                .and("event.status").as("status")
                .and("event.eventType").as("eventType")
                .and("event.createdAt").as("createdAt");

        SortOperation sortOperation = new SortOperation(Sort.by(Sort.Direction.DESC, "event.createdAt"));

        Aggregation aggregation = Aggregation.newAggregation(
                matchUserOrders,
                convertEventIdToObjectId,
                lookupOperation,
                unwindOperation,
                matchEventStatus,
                projectionOperation,
                sortOperation
        );

        AggregationResults<OrderActivityResponse> results = mongoTemplate.aggregate(aggregation, "orders", OrderActivityResponse.class);

        return results
                .getMappedResults()
                .stream()
                .map(converterService::convertToOrderActiveResponse)
                .collect(Collectors.toList());

    }

}
