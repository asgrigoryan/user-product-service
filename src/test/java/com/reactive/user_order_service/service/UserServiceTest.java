package com.reactive.user_order_service.service;

import com.reactive.userorderservice.client.OrderServiceClient;
import com.reactive.userorderservice.client.ProductServiceClient;
import com.reactive.userorderservice.model.User;
import com.reactive.userorderservice.repository.UserRepository;
import com.reactive.userorderservice.service.UserService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private static final int WIREMOCK_PORT = 8087;

    private WireMockServer wireMockServer;
    private OrderServiceClient orderServiceClient;
    private ProductServiceClient productServiceClient;
    private UserRepository userRepository;
    private UserService userService;

    @BeforeAll
    void startWireMock() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setup() {
        WebClient.Builder webClientBuilder = WebClient.builder();

        orderServiceClient = new OrderServiceClient(webClientBuilder, "http://localhost:" + WIREMOCK_PORT);
        productServiceClient = new ProductServiceClient(webClientBuilder, "http://localhost:" + WIREMOCK_PORT);

        userRepository = mock(UserRepository.class);

        userService = new UserService(userRepository, orderServiceClient, productServiceClient);
    }

    @Test
    void getProductWithHighestScoreByUserId_success() {
        // Mock user repository
        when(userRepository.findById("user1")).thenReturn(Mono.just(new User("user1", "Alice", "0000000000")));

        // Mock orders
        stubFor(get(urlPathEqualTo("/order/phone"))
                .withQueryParam("phoneNumber", equalTo("0000000000"))
                .willReturn(okJson("[{\"phoneNumber\":\"0000000000\",\"orderNumber\":\"1\",\"productCode\":\"1234\"}]")
                        .withHeader("Content-Type", "application/json")));

        // Mock products
        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("1234"))
                .willReturn(okJson("[{\"productId\":\"p1\",\"productCode\":\"1234\",\"productName\":\"Product A\",\"score\":9.5}]")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(userService.getProductWithHighestScoreByUserId("user1"))
                .expectNextMatches(product ->
                        "1234".equals(product.getProductCode()) &&
                                "Product A".equals(product.getProductName()) &&
                                product.getScore() == 9.5
                )
                .verifyComplete();
    }

    @Test
    void getProductWithHighestScoreByUserId_noProducts_throwsNotFound() {
        // Mock user repository
        when(userRepository.findById("user2")).thenReturn(Mono.just(new User("user2", "Bob", "0000000001")));

        // Mock orders returns empty list
        stubFor(get(urlPathEqualTo("/order/phone"))
                .withQueryParam("phoneNumber", equalTo("0000000001"))
                .willReturn(okJson("[]")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(userService.getProductWithHighestScoreByUserId("user2"))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND
                )
                .verify();
    }

    @Test
    void getProductsFromOrders_success() {
        Flux<String> productCodes = Flux.just("9999");

        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("9999"))
                .willReturn(okJson("[{\"productId\":\"p9\",\"productCode\":\"9999\",\"productName\":\"Product Z\",\"score\":7.5}]")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(userService.getProductsFromProductCodes(productCodes))
                .expectNextMatches(product -> "9999".equals(product.getProductCode()) && product.getScore() == 7.5)
                .verifyComplete();
    }

    @Test
    void getProductsFromOrders_partialFailure_returnsEmptyForFailedProduct() {
        Flux<String> productCodes = Flux.just("1111", "2222");

        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("1111"))
                .willReturn(okJson("[{\"productId\":\"p11\",\"productCode\":\"1111\",\"productName\":\"Product X\",\"score\":8.0}]")
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("2222"))
                .willReturn(okJson("INVALID_JSON")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(userService.getProductsFromProductCodes(productCodes))
                .expectNextMatches(product -> "1111".equals(product.getProductCode()) && product.getScore() == 8.0)
                .expectComplete()
                .verify();
    }
}
