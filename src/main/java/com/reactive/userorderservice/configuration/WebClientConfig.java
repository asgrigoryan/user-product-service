package com.reactive.userorderservice.configuration;

import com.reactive.userorderservice.filter.constants.LogKeys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        ExchangeFilterFunction propagateRequestId = (request, next) ->
                Mono.deferContextual(ctxView -> {
                    String rid = ctxView.getOrDefault(LogKeys.REQUEST_ID, "");
                    ClientRequest mutated = ClientRequest.from(request)
                            .header(LogKeys.REQUEST_ID, rid)
                            .build();
                    return next.exchange(mutated);
                });

        return builder
                .filter(propagateRequestId)
                .build();
    }
}

