package com.example.KavaSpring.helper;

import com.example.KavaSpring.helper.dto.BrewEventResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class BrewEventAggregation {

    private final String id;


    public BrewEventAggregation(String id) {
        this.id = id;
    }

    /*List<BrewEventResult> aggregateBrewEvents() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("creator.$id")
                .foreignField("_id")
                .as("creator");

        UnwindOperation unwindOperation = Aggregation.unwind("creator");

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("creator._id").ne(new ObjectId(id))
        );

        GroupOperation groupOperation = Aggregation.group()
                .addToSet("_id").as("eventId")
                .addToSet("creator.firstName").as("firstName")
                .addToSet("creator.lastName").as("lastName");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupOperation,
                unwindOperation,
                matchOperation,
                groupOperation
        );

        AggregationResults<BrewEventResult> results = mongoTemplate.aggregate(
                aggregation, "brewEvents", BrewEventResult.class);

        return results.getMappedResults();
    }*/
}
