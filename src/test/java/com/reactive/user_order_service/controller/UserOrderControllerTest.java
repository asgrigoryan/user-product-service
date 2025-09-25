package com.reactive.user_order_service.controller;

import com.reactive.userorderservice.controller.UserOrderController;
import com.reactive.userorderservice.model.Product;
import com.reactive.userorderservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserOrderControllerTest {

    private WebTestClient webTestClient;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        UserOrderController controller = new UserOrderController(userService);

        webTestClient = WebTestClient.bindToController(controller)
                .build();
    }

    @Test
    void getOrdersByUserId_returnsProduct() {
        Product mockProduct = new Product("p1", "1234", "Product A", 9.5);
        when(userService.getProductWithHighestScoreByUserId(anyString()))
                .thenReturn(Mono.just(mockProduct));

        webTestClient.get()
<<<<<<< HEAD
                .uri("/user-order-service/orders/user/user1")
=======
                .uri("/userOrderService/orders/user/user1")
>>>>>>> main
                .header("X-Request-Id", "test-request-id")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_NDJSON)
                .expectBody(Product.class)
                .value(product -> {
                    assert product.getProductId().equals("p1");
                    assert product.getProductCode().equals("1234");
                    assert product.getProductName().equals("Product A");
                    assert product.getScore() == 9.5;
                });
    }

    @Test
    void getOrdersByUserId_userNotFound_returnsNotFound() {
        when(userService.getProductWithHighestScoreByUserId(anyString()))
<<<<<<< HEAD
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/user-order-service/orders/user/nonexistent")
                .header("X-Request-Id", "test-request-id")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound();
=======
                .thenReturn(Mono.error(new RuntimeException("User not found")));

        webTestClient.get()
                .uri("/userOrderService/orders/user/nonexistent")
                .header("X-Request-Id", "test-request-id")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().is5xxServerError();
>>>>>>> main
    }
}
