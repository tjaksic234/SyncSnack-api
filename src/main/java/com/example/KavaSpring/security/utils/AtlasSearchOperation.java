package com.example.KavaSpring.security.utils;

import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

@AllArgsConstructor
public class AtlasSearchOperation implements AggregationOperation {

    private final String query;

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return new Document("$search",
                new Document("index", "additionalOptions")
                        .append("autocomplete",
                                new Document("query", query)
                                        .append("path", "additionalOptions.description")
                        )
        );
    }
}
