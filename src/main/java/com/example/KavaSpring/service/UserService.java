package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dto.CoffeeOrderDto;
import com.example.KavaSpring.models.dto.GetEventsForUserRequest;
import com.example.KavaSpring.models.dto.GetUserResponse;
import com.example.KavaSpring.models.dto.GetUsersResponse;

import java.util.List;

public interface UserService {

        List<GetUsersResponse> getAll();
        GetUserResponse getUserById(String id);
        List<CoffeeOrderDto> getOrdersForUser(String id);
        BrewEvent getBrewEventsForUser(String id);
        String getEventForOrder(GetEventsForUserRequest request);
}
