package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dto.BrewEventResult;

import java.util.List;

public interface BrewEventAggregationService {

    List<BrewEventResult> aggregateBrewEvents();
}
