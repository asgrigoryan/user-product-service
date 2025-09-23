package com.reactive.user_order_service.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.reactive.userorderservice.client.ProductServiceClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceClientTest {

    private static final int WIREMOCK_PORT = 8086;
    private WireMockServer wireMockServer;
    private ProductServiceClient productServiceClient;

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
    void setupClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        productServiceClient = new ProductServiceClient(webClientBuilder,
                "http://localhost:" + WIREMOCK_PORT);
    }

    @Test
    void getProductByCode_validCode_returnsProducts() {
        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("1234"))
                .willReturn(okJson("[{" +
                        "\"productId\":\"1\"," +
                        "\"productCode\":\"1234\"," +
                        "\"productName\":\"meal\"," +
                        "\"score\":99" +
                        "}]")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(productServiceClient.getProductByCode("1234"))
                .expectNextMatches(products ->
                        products.size() == 1 &&
                                "1234".equals(products.get(0).getProductCode()) &&
                                "meal".equals(products.get(0).getProductName()) &&
                                products.get(0).getScore() == 99
                )
                .verifyComplete();
    }

    @Test
    void getProductByCode_notFound_returnsEmptyList() {
        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("9999"))
                .willReturn(notFound()));

        StepVerifier.create(productServiceClient.getProductByCode("9999"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void getProductByCode_error_returnsEmptyList() {
        stubFor(get(urlPathEqualTo("/product/names"))
                .withQueryParam("productCode", equalTo("error"))
                .willReturn(okJson("INVALID_JSON")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(productServiceClient.getProductByCode("error"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }
}
