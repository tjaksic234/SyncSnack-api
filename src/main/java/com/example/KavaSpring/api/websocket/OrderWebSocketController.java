package com.example.KavaSpring.api.websocket;

import com.example.KavaSpring.models.dto.OrderRequest;
import com.example.KavaSpring.models.dto.OrderResponse;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
@Slf4j
@AllArgsConstructor
public class OrderWebSocketController {

    private final OrderService orderService;

    @MessageMapping("/order")
    @SendTo("/topic/newOrder")
    public OrderResponse handleOrder(OrderRequest request) {
        return orderService.createOrder(request);
    }

    @MessageMapping("/welcome")
    @SendTo("/topic/greetings")
    public String handleWelcomeMessage(String message) {
        log.info("Received message: {}", message);
        return "Welcome to the WebSocket server, " + HtmlUtils.htmlEscape(message) + "!";
    }
}
