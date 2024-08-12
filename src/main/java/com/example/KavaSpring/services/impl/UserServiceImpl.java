
package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.services.UserService;
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

    private final ConverterService converterService;

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));


        log.info("Get user by id finished");
        return converterService.convertToUserDto(user);
    }

    @Override
    public boolean checkEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);

        if (!exists) {
            throw new NotFoundException("The provided email is not correct");
        }
        return true;
    }

    @Override
    public boolean isUserVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return user.isVerified();
    }


}

