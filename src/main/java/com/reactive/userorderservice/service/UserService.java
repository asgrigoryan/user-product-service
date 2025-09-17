package com.reactive.userorderservice.service;

import com.reactive.userorderservice.client.OrderServiceClient;
import com.reactive.userorderservice.client.ProductServiceClient;
import com.reactive.userorderservice.model.Order;
import com.reactive.userorderservice.model.Product;
import com.reactive.userorderservice.model.User;
import com.reactive.userorderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OrderServiceClient orderServiceClient;
    private final ProductServiceClient productServiceClient;


    public Mono<String> getUserPhoneNumberById(String userId) {
        return userRepository.findById(userId)
                .map(User::getPhoneNumber)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    public Flux<Order> getOrdersByPhoneNum(String userId) {
        return getUserPhoneNumberById(userId)
                .flatMapMany(phoneNumber -> {
                    log.info("Fetching orders for phone number: {}", phoneNumber);
                    return orderServiceClient.getOrdersByPhoneNumber(phoneNumber);
                });
    }

    public Mono<Product> getProductWithHighestScoreByUserId(String userId) {
        Flux<Order> orders = this.getOrdersByPhoneNum(userId);
        Flux<String> productCodes = orders.map(Order::getProductCode);
        Flux<Product> products = this.getProductsFromProductCodes(productCodes);
        return this.getProductWithHighestScore(products);
    }


    public Flux<Product> getProductsFromProductCodes(Flux<String> productCodes) {
        return productCodes.flatMap(productCode -> productServiceClient.getProductByCode(productCode)
                                .doOnNext(products -> log.info("Fetched {} products for productCode={}", products.size(), productCode))
                                .onErrorResume(e -> {
                                    log.error("Error fetching products for productCode={}: {}", productCode, e.getMessage());
                                    return Mono.just(List.of());
                                }).flatMapMany(Flux::fromIterable)
                );
    }

    public Mono<Product> getProductWithHighestScore(Flux<Product> products) {
        return products
                .reduce((p1, p2) -> p1.getScore() >= p2.getScore() ? p1 : p2)
                .doOnNext(product -> log.info("Selected product with highest score: code={}, score={}", product.getProductCode(), product.getScore()))
                .switchIfEmpty(Mono.error(new IllegalStateException("No products found")));
    }
}