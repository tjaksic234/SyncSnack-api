package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.EventAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.EventService;
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

    @Override
    public EventResponse createEvent(EventRequest request) {
        //? Logiku provjere eventova za usera ce trebati popraviti jer creator moze imati samo jedan event nebitno jeli completed,
        //? pending ili inprogress pa treba jos poraditi na logici
        //* Ovakav nacin rada sa helper klasom mi je malo cudan za sada treba viditi koliko je ovo pametno za raditi
        log.info("The user id which will be used for searching the user profile --> {}", Helper.getLoggedInUserId());
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        log.info("The user profile that was found with the userId --> {}", userProfile);
        if (userProfile == null) {
            throw new NotFoundException("No UserProfile associated with the id");
        }

        List<Event> existingActiveEvents = eventRepository.findByUserProfileIdAndStatus(userProfile.getId(), EventStatus.PENDING);

        if (!existingActiveEvents.isEmpty()) {
            throw new EventAlreadyExistsException("User already has an active event (PENDING or IN_PROGRESS)");
        } else {
            log.info("No active events found for userProfileId: {}. Event creation continues.", userProfile.getId());
        }

        Event event = new Event();
        event.setUserProfileId(userProfile.getId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setGroupId(userProfile.getGroupId());
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
    public List<EventDto> searchEvents(EventSearchRequest request) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        List<Criteria> criteriaList = new ArrayList<>();

        if (request.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getEventType() != null) {
            criteriaList.add(Criteria.where("eventType").is(request.getEventType()));
        }

        criteriaList.add(Criteria.where("groupId").is(userProfile.getGroupId()));
        criteriaList.add(Criteria.where("userProfileId").ne(userProfile.getId()));

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
    //? trenutno ce azurirati svake minute
    @Scheduled(cron = "0 */20 * * * * ")
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


}
