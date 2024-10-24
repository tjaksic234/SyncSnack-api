package com.example.KavaSpring.security.utils;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dto.OrderNotification;


public class FirebaseMessageTemplates {

    public static final String NEW_ORDER_TITLE = "New order placed \u200B\u200B\uD83D\uDECE\uFE0F";
    public static final String NEW_EVENT_TITLE = "\uD83C\uDF7D\uFE0F\u200B New event \uD83D\uDE0B\u200B";

    public static String buildNewOrderContent(OrderNotification orderNotification, String groupName) {
        return orderNotification.getFirstName() +
                " " +
                orderNotification.getLastName() +
                " placed an order for group " +
                groupName;
    }

    public static String buildNewEventContent(Event event, String groupName) {
        return event.getTitle() +
                "\n\n" +
                "\uD83D\uDCDD " + event.getDescription() +  // 📝 for description
                "\n\n" +
                "\uD83D\uDC65 " + groupName;               // 👥 for group
    }
}

