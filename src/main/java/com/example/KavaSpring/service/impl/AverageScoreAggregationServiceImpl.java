package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dto.UserCoffeeStats;
import com.example.KavaSpring.service.AverageScoreAggregationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AverageScoreAggregationServiceImpl implements AverageScoreAggregationService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AverageScoreAggregationServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserCoffeeStats> calculate(List<CoffeeOrder> orders) {

        List<String> userIds = orders.stream()
                .map(CoffeeOrder::getUserId)
                .distinct()
                .toList();

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("userId").in(userIds)
                        .and("rating").ne(0));

        GroupOperation groupOperation = Aggregation.group("userId")
                .avg("rating").as("averageRating")
                .first("userId").as("userId");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation
        );

        AggregationResults<UserCoffeeStats> results = mongoTemplate.aggregate(
                aggregation, "coffeeOrders", UserCoffeeStats.class);

         return results.getMappedResults();
    }
}
