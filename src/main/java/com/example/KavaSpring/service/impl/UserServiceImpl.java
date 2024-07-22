
package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());

        log.info("Get user by id finished");
        return userDto;
    }



}

