package com.example.KavaSpring.security.services.impl;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email" + email));

        UserProfile userProfile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("UserProfile not found for user " + user.getId()));

        return UserDetailsImpl.build(user, userProfile.getId());
    }
}
