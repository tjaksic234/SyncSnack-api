package com.example.KavaSpring.api.websocket;

import com.example.KavaSpring.services.OrderService;
import com.example.KavaSpring.services.WebSocketService;
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

    //? everything sent to the server has a prefix /app e.g. app/welcome

    private final OrderService orderService;

    private final WebSocketService webSocketService;

   /* @MessageMapping("/order")
    @SendTo("/topic/newOrder")
    public OrderResponse handleOrder(OrderRequest request) {
        return orderService.createOrder(request);
    }
*/
/*    @MessageMapping("/welcome")
    @SendTo("/topic/greetings")
    public String handleWelcomeMessage(MessageDto message) {
        log.info("Received message: {}", message);
        return "Welcome to the WebSocket server, " + HtmlUtils.htmlEscape(message.getName()) + "!";
    }*/

   /* @MessageMapping("/disconnect")
    public void handleDisconnect(String username) {
        webSocketService.removeUser(username);
    }*/
}
