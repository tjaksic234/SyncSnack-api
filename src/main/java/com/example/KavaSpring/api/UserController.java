package com.example.KavaSpring.api;

import com.example.KavaSpring.exceptions.EmptyContentException;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    /*@GetMapping
    public ResponseEntity<List<GetUsersResponse>> getAll() {
        try {
            log.info("Getting all users");
            return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("{id}/orders")
    public ResponseEntity<List<CoffeeOrderDto>> getOrdersForUser(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(userService.getOrdersForUser(id), HttpStatus.OK);
        } catch (EntityNotFoundException | EmptyContentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @GetMapping("{userId}/events")
    public ResponseEntity<BrewEvent> getBrewEventsForUser(@PathVariable("userId") String userId) {
        try {
            return new ResponseEntity<>(userService.getBrewEventsForUser(userId), HttpStatus.OK);
        } catch (NullPointerException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("events")
    public ResponseEntity<String> getEventForOrder(@RequestBody GetEventsForUserRequest request) {

        try {
            return new ResponseEntity<>(userService.getEventForOrder(request), HttpStatus.OK);
        } catch (NullPointerException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }*/

}
