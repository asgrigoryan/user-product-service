package com.reactive.userorderservice.client;

import com.reactive.userorderservice.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ProductServiceClient {

    private final WebClient webClient;

    public ProductServiceClient(WebClient.Builder webClientBuilder,
            @Value("${product.service.baseurl}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<List<Product>> getProductByCode(String productCode) {
        log.info("Calling Product Service for code={} ", productCode);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/product/names")
                        .queryParam("productCode", productCode)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> {
                    log.error("Error fetching products for code={} ", productCode, ex);
                    return Mono.just(Collections.emptyList());
                })
                .map(products -> products != null ? products : Collections.<Product>emptyList())
                .doOnNext(products -> log.info("Fetched {} products for code={} ", products.size(), productCode));
    }
}
