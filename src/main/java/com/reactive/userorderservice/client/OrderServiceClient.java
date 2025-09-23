package com.reactive.userorderservice.client;

import com.reactive.userorderservice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Component
public class OrderServiceClient {

    private final WebClient webClient;

    public OrderServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${order.service.baseurl}") String baseUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Flux<Order> getOrdersByPhoneNumber(String phoneNumber) {
        String requestUri = "/order/phone?phoneNumber=" + phoneNumber;

        log.info(LogMessages.CALLING_ORDER_SERVICE, phoneNumber);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/order/phone")
                        .queryParam("phoneNumber", phoneNumber)
                        .build())
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(Order.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .flatMapMany(orders -> {
                    if (orders.isEmpty()) {
                        log.warn(LogMessages.NO_ORDERS_FOUND, phoneNumber);
                        return Flux.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No orders found for phoneNumber=" + phoneNumber
                        ));
                    }
                    log.info(LogMessages.FETCHED_ORDERS, orders.size(), phoneNumber);
                    return Flux.fromIterable(orders);
                })
                .doOnError(ex -> log.error(
                        LogMessages.ERROR_FETCHING_ORDERS,
                        requestUri, phoneNumber, ex.getMessage(), ex
                ));
    }
}
