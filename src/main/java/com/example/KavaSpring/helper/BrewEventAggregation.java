package com.example.KavaSpring.helper;

import com.example.KavaSpring.helper.dto.BrewEventResult;
import com.example.KavaSpring.models.dao.enums.EventStatus;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
public class BrewEventAggregation {

    private static final Logger log = LoggerFactory.getLogger(BrewEventAggregation.class);
    private String id;

    private final MongoTemplate mongoTemplate;

    public BrewEventAggregation(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<BrewEventResult> aggregateBrewEvents() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("creator.$id")
                .foreignField("_id")
                .as("creator");

        UnwindOperation unwindOperation = Aggregation.unwind("creator");

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("creator._id").ne(new ObjectId(id))
                        .and("status").is(EventStatus.PENDING)
        );

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("eventId")
                .and("creator.firstName").as("firstName")
                .and("creator.lastName").as("lastName")
                .and("status").as("status");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupOperation,
                unwindOperation,
                matchOperation,
                projectionOperation
        );

        AggregationResults<BrewEventResult> results = mongoTemplate.aggregate(
                aggregation, "brewEvents", BrewEventResult.class);

        log.info("Aggregation results: " + results.getMappedResults());
        return results.getMappedResults();
    }
}
