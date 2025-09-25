package com.reactive.userorderservice.controller;

import com.reactive.userorderservice.model.Product;
import com.reactive.userorderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user-order-service")
@RequiredArgsConstructor
public class UserOrderController {

 private final UserService userService;

    @GetMapping(value = "/orders/user/{userId}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<Product> getOrdersByUserId(@PathVariable String userId, @RequestHeader(value = "X-Request-Id") String requestId) {
        if (StringUtils.isBlank(userId)) {
            return Mono.error(new IllegalArgumentException("userId cannot be empty"));
        }
        return userService.getProductWithHighestScoreByUserId(userId)
                .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND)));
    }
}