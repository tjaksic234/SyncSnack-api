package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.EventAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dto.EventDto;
import com.example.KavaSpring.models.dto.EventRequest;
import com.example.KavaSpring.models.dto.EventResponse;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.services.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    private final MongoTemplate mongoTemplate;

    @Override
    public EventResponse createEvent(EventRequest request) {
        //? Logiku provjere eventova za usera ce trebati popraviti jer creator moze imati samo jedan event nebitno jeli completed,
        //? pending ili inprogress pa treba jos poraditi na logici

        List<Event> existingActiveEvents = eventRepository.findByCreatorIdAndStatus(request.getCreatorId(), EventStatus.PENDING);

        if (!existingActiveEvents.isEmpty()) {
            throw new EventAlreadyExistsException("User already has an active event (PENDING or IN_PROGRESS)");
        } else {
            log.info("No active events found for creatorId: {}. Event creation continues.", request.getCreatorId());
        }

        Event event = new Event();
        event.setCreatorId(request.getCreatorId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setGroupId(request.getGroupId());
        event.setEventType(request.getEventType());
        event.setPendingUntil(LocalDateTime.now().plusMinutes(request.getPendingTime()));
        eventRepository.save(event);

        log.info("Event created");
        return converterService.convertToEventResponse(request);
    }

    @Override
    public EventDto getEventById(String id) {
        Event event = eventRepository.getById(id).orElseThrow(() -> new NotFoundException("No event associated with the id"));
        log.info("Get event by id finished");
        return converterService.convertToEventDto(event);
    }

    @Override
    public List<EventDto> searchEvents(EventStatus status, EventRequest request) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (request.getEventType() != null) {
            criteriaList.add(Criteria.where("eventType").is(request.getEventType()));
        }

        criteriaList.add(Criteria.where("groupId").is(request.getGroupId()));
        criteriaList.add(Criteria.where("creatorId").ne(request.getCreatorId()));

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        MatchOperation matchOperation = Aggregation.match(combinedCriteria);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                sortOperation
        );

        AggregationResults<Event> results = mongoTemplate.aggregate(aggregation, "events", Event.class);

        return results
                .getMappedResults()
                .stream()
                .map(converterService::convertToEventDto)
                .collect(Collectors.toList());
    }

    //? Cron expression: sec min hrs day mon weekday
    @Scheduled(cron = "0 */1 * * * * ")
    @Override
    public void updateEvents() {


    }

}
