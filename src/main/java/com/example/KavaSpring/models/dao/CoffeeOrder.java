package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.CoffeeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coffeeOrders")
@Getter @Setter
public class CoffeeOrder {

    @Id
    private String coffeeOrderId;

    @NotBlank
    private String eventId;

    @NotBlank
    private String userId;

    @NotBlank
    private CoffeeType type;

    @NotNull
    @Min(0)
    @Max(5)
    private int sugarQuantity;

    @NotNull
    @Min(0)
    @Max(5)
    private int milkQuantity;

    @NotNull
    @Min(0)
    @Max(5)
    private int rating;

    public CoffeeOrder(String userId, String eventId, CoffeeType type, int sugarQuantity, int milkQuantity, int rating) {
        this.userId = userId;
        this.eventId = eventId;
        this.type = type;
        this.sugarQuantity = sugarQuantity;
        this.milkQuantity = milkQuantity;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "CoffeeOrder{" +
                "coffeeOrderId='" + coffeeOrderId + '\'' +
                ", type=" + type +
                ", sugarQuantity=" + sugarQuantity +
                ", milkQuantity=" + milkQuantity +
                '}';
    }
}
