package com.reactive.user_order_service.client;

import com.reactive.userorderservice.client.OrderServiceClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceClientTest {

    private static final int WIREMOCK_PORT = 8080;
    private WireMockServer wireMockServer;
    private OrderServiceClient orderServiceClient;

    @BeforeAll
    void startWireMock() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setupClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        orderServiceClient = new OrderServiceClient(webClientBuilder, "http://localhost:" + WIREMOCK_PORT);
    }

    @Test
    void getOrdersByPhoneNumber_validNumber_returnsOrders() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/order/phone"))
                .withQueryParam("phoneNumber", WireMock.equalTo("0000000000"))
                .willReturn(WireMock.okJson("[{" +
                                "\"phoneNumber\":\"123456789\"," +
                                "\"orderNumber\":\"1\"," +
                                "\"productCode\":\"1234\"" +
                                "}]")
                        .withHeader("Content-Type", "application/x-ndjson")));

        StepVerifier.create(orderServiceClient.getOrdersByPhoneNumber("0000000000"))
                .expectNextMatches(order ->
                        "123456789".equals(order.getPhoneNumber()) &&
                                "1".equals(order.getOrderNumber()) &&
                                "1234".equals(order.getProductCode())
                )
                .verifyComplete();
    }

    @Test
    void getOrdersByPhoneNumber_notFound_throwsWebClientResponseException() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/order/phone"))
                .withQueryParam("phoneNumber", WireMock.equalTo("0000000001"))
                .willReturn(WireMock.notFound()));

        StepVerifier.create(orderServiceClient.getOrdersByPhoneNumber("0000000001"))
                .expectError(WebClientResponseException.NotFound.class)
                .verify();
    }
}
