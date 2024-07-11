package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.CoffeeType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoffeeOrderRepository extends MongoRepository<CoffeeOrder, String> {

    Optional<CoffeeOrder> findByCoffeeOrderId(String coffeeOrderId);
    boolean existsByCoffeeOrderId(String coffeeOrderId);
}
