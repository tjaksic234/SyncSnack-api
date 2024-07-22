package com.example.KavaSpring.service;

import java.util.List;

public interface UserService {

        List<GetUsersResponse> getAll();
        GetUserResponse getUserById(String id);
        List<CoffeeOrderDto> getOrdersForUser(String id);
        String getEventForOrder(GetEventsForUserRequest request);
}
