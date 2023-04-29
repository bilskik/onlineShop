package com.bilskik.onlineshop.entities;

import com.bilskik.onlineshop.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    private int orderId;
    @JsonProperty("orderDate")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Date of order cannot be null!")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Long total;
    @OneToOne
    @JoinColumn(
            name = "customerId",
            referencedColumnName = "customerId"
    )
    private Customer customer;
    @OneToOne
    @JoinColumn(
            name = "cartId",
            referencedColumnName = "cartId"
    )
    private Cart cart;

}
