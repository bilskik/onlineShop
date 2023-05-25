package com.bilskik.onlineshop.controllers;

import com.bilskik.onlineshop.dto.AddressDTO;
import com.bilskik.onlineshop.dto.OrderDTO;
import com.bilskik.onlineshop.embedded.Address;
import com.bilskik.onlineshop.entities.Order;
import com.bilskik.onlineshop.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/orders")
    public ResponseEntity<OrderDTO> getOrder() {
        return new ResponseEntity<>(orderService.getOrder(), HttpStatusCode.valueOf(200));
    }
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody Order order) {
        return new ResponseEntity<>(orderService.saveOrder(order), HttpStatusCode.valueOf(200));
    }
    @PutMapping("/orders")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody Order order) {
        return new ResponseEntity<>(orderService.updateAddress(order), HttpStatusCode.valueOf(200));
    }
}
