package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.OrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final ConverterService converterService;
    private final MongoTemplate mongoTemplate;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        boolean existsOrder = userRepository.existsById(request.getOrderedBy());
        boolean existsEvent = eventRepository.existsById(request.getEventId());


        if (!existsOrder) {
            throw new NotFoundException("No user associated with id");
        }

        if (!existsEvent) {
            throw new NotFoundException("No event associated with eventId in the order");
        }

        log.info("the order request is: {}", request);
        Order order = new Order();
        order.setOrderedBy(request.getOrderedBy());
        order.setEventId(request.getEventId());
        order.setAdditionalOptions(request.getAdditionalOptions());
        orderRepository.save(order);

        log.info("Order created");
        return converterService.convertToOrderResponse(request);
    }

    @Override
    public OrderDto getOrderById(String id) {
        Order order = orderRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("No order associated with id"));

        log.info("Get order by id finished");
        return converterService.convertToOrderDto(order);
    }

    @Override
    public List<OrderActiveResponse> activeOrders(OrderActiveRequest request) {

        if (request.getUserProfileId() == null) {
            throw new NullPointerException("Bad UserProfileId value");
        }

        MatchOperation matchOperation1 = Aggregation.match(Criteria.where("orderedBy").is(request.getUserProfileId()));


        AddFieldsOperation setOperation = Aggregation.addFields()
                .addField("eventId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$eventId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("events", "eventId", "_id", "event");

        UnwindOperation unwindOperation = Aggregation.unwind("event");

        MatchOperation matchOperation2 = Aggregation.match(Criteria.where("event.status").in( EventStatus.IN_PROGRESS));

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("event._id").as("eventId")
                .and("_id").as("orderId")
                .and("event.creatorId").as("creatorId")
                .and("event.title").as("title")
                .and("event.description").as("description")
                .and("event.groupId").as("groupId")
                .and("event.status").as("status")
                .and("event.eventType").as("eventType")
                .and("event.createdAt").as("createdAt");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation1,
                setOperation,
                lookupOperation,
                unwindOperation,
                matchOperation2,
                projectionOperation
        );

        AggregationResults<OrderActiveResponse> results = mongoTemplate.aggregate(aggregation, "orders", OrderActiveResponse.class);

        return results
                .getMappedResults()
                .stream()
                .map(converterService::convertToOrderActiveResponse)
                .collect(Collectors.toList());

    }
}
