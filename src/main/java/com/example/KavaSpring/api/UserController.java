package com.example.KavaSpring.api;

import com.example.KavaSpring.api.dto.CoffeeOrderDto;
import com.example.KavaSpring.api.dto.GetBrewEventHistoryResponse;
import com.example.KavaSpring.api.dto.GetEventsForUserRequest;
import com.example.KavaSpring.api.dto.GetUsersResponse;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    @Autowired
    private BrewEventRepository brewEventRepository;

    @GetMapping
    public ResponseEntity<List<GetUsersResponse>> getAll() {

        List<GetUsersResponse> users = userRepository.getAllBy();

        if (users.isEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body("The user collection is empty.");
        }


        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // This retrieves all the orders associated with the user that called this method
    @GetMapping("/orders/{id}")
    public ResponseEntity<List<CoffeeOrderDto>> getOrders(@PathVariable("id") String id) {
        List<CoffeeOrder> orders = coffeeOrderRepository.findByUserId(id);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<CoffeeOrderDto> orderDTOs = orders.stream()
                    .map(order -> new CoffeeOrderDto(
                            order.getCoffeeOrderId(),
                            order.getEventId(),
                            order.getUserId(),
                            order.getType(),
                            order.getSugarQuantity(),
                            order.getMilkQuantity(),
                            order.getRating()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        }
    }

    @GetMapping("events")
    public ResponseEntity<String> getEventForOrder(@RequestBody GetEventsForUserRequest request) {

        BrewEvent event = brewEventRepository.findByUserIdAndOrderIdsContaining(request.getUserId(), request.getCoffeeOrderId());

        if (event == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(event.getEventId());

    }

    // retrieve all brew events of the specific user
    @GetMapping("history/{userId}")
    public ResponseEntity<GetBrewEventHistoryResponse> getBrewEventHistory(@PathVariable("userId") String userId) {
        BrewEvent event = brewEventRepository.findByUserId(userId);

        GetBrewEventHistoryResponse response = new GetBrewEventHistoryResponse();

        response.setStartTime(event.getStartTime());
        response.setOrderIds(event.getOrderIds());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
