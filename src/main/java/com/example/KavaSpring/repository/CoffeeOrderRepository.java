package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoffeeOrderRepository extends MongoRepository<CoffeeOrder, String> {

    CoffeeOrder findByCoffeeOrderId(String coffeeOrderId);
    boolean existsByCoffeeOrderId(String coffeeOrderId);
    List<CoffeeOrder> findByUserId(String userId);

    // Custom method to update the coffee order rating across collections in the database
    @Query("{ '_id': ?0 }")
    @Update("{ '$set':  { 'rating':  ?1 } }")
    int updateRating(String orderId, int newRating);

}
