package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.GetUserResponse;
import com.example.KavaSpring.models.dto.GetUsersResponse;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<GetUsersResponse> getAll() {
        List<GetUsersResponse> users = userRepository.findAll()
                .stream().map(user -> {
                    GetUsersResponse response = new GetUsersResponse();
                    response.setEmail(user.getEmail());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setCoffeeCounter(user.getCoffeeNumber());
                    //response.setCoffeeRating(Float.parseFloat(String.format("%.2f", user.getScore())));
                    response.setCoffeeRating(user.getScore());
                    return response;
                })
                .toList();

        if (users.isEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body("The user collection is empty.");
        }
        return users;
    }

    @Override
    public GetUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GetUserResponse userResponse = new GetUserResponse();

        userResponse.setUserId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCoffeeNumber(user.getCoffeeNumber());
        userResponse.setScore(user.getScore());

        return userResponse;
    }
}
