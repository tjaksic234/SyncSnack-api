package com.example.KavaSpring.security.services;

import com.example.KavaSpring.models.dao.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter @Setter
public class UserDetailsImpl implements UserDetails {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private int coffeeCounter;
    private int coffeeRating;

    private String password;

    public UserDetailsImpl(String id, String email, String firstName,
                           String lastName, int coffeeCounter,
                           int coffeeRating, String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coffeeCounter = coffeeCounter;
        this.coffeeRating = coffeeRating;
        this.password = password;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCoffeeCounter(),
                user.getCoffeeRating(),
                user.getPassword()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}