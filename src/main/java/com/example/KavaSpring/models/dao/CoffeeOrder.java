package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "coffeeOrders")
@Getter @Setter
public class CoffeeOrder {

    @Id
    private String coffeeOrderId;

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

    public CoffeeOrder(CoffeeType type, int sugarQuantity, int milkQuantity) {
        this.type = type;
        this.sugarQuantity = sugarQuantity;
        this.milkQuantity = milkQuantity;
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
