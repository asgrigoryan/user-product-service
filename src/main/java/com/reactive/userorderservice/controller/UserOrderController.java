package com.reactive.userorderservice.controller;

import com.reactive.userorderservice.model.Product;
import com.reactive.userorderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/userOrderService")
@RequiredArgsConstructor
public class UserOrderController {

 private final UserService userService;

    @GetMapping(value = "/orders/user/{userId}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<Product> getOrdersByUserId(@PathVariable String userId, @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        return userService.getProductWithHighestScoreByUserId(userId);
    }
}