package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.EventCoffeeStats;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.ScoreSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ScoreSchedulerServiceImpl implements ScoreSchedulerService {


    private final UserRepository userRepository;

    private final BrewEventRepository brewEventRepository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ScoreSchedulerServiceImpl(UserRepository userRepository, BrewEventRepository brewEventRepository,
                                     MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.brewEventRepository = brewEventRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void updateScore() {

        //* Retrieve all the event IDs available
        /*List<String> eventIds = brewEventRepository.findAll()
                .stream()
                .map(BrewEvent::getEventId)
                .distinct()
                .toList();
        */
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("rating").gt(0));

        GroupOperation groupOperation = Aggregation.group("eventId")
                .avg("rating").as("averageRating")
                .count().as("coffeeCount")
                .first("eventId").as("eventId");

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("_id")
                .and("eventId").as("eventId")
                .and("averageRating").as("averageRating")
                .and("coffeeCount").as("coffeeCount");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                projectionOperation
        );

        AggregationResults<EventCoffeeStats> results = mongoTemplate.aggregate(
                aggregation, "coffeeOrders", EventCoffeeStats.class);

        List<EventCoffeeStats> eventCoffeeStats = results.getMappedResults();


        for (EventCoffeeStats stats : eventCoffeeStats) {
            String eventId = stats.getEventId();
            log.info("EventCoffeeStats: eventId={}, averageRating={}, coffeeCount={}",
                    stats.getEventId(), stats.getAverageRating(), stats.getCoffeeCount());
            if (eventId != null) {
                BrewEvent brewEvent = brewEventRepository.findById(eventId).orElse(null);
                if (brewEvent != null) {
                    String userId = brewEvent.getUserId();
                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null && stats.getCoffeeCount() > 0) {
                        user.setScore(stats.getAverageRating());
                        user.setCoffeeNumber(stats.getCoffeeCount());
                        userRepository.save(user);

                        log.info("Updated user {} with score {} and coffee count {}",
                                userId, stats.getAverageRating(), stats.getCoffeeCount());
                    } else {
                        log.warn("User not found for ID: {}", userId);
                    }
                } else {
                    log.warn("BrewEvent not found for ID: {}", eventId);
                }
            } else {
                log.warn("Encountered a null eventId in EventCoffeeStats");
            }
        }

    }
}
