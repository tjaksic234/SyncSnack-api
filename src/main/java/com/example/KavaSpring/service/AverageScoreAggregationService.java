package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dto.UserCoffeeStats;

import java.util.List;

public interface AverageScoreAggregationService {

    List<UserCoffeeStats> calculate(List<CoffeeOrder> orders);
}
