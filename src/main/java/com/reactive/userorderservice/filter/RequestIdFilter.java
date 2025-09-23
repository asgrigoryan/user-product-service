package com.reactive.userorderservice.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdFilter implements WebFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        final String id = (requestId == null || requestId.isEmpty()) ? UUID.randomUUID().toString() : requestId;

        MDC.put(REQUEST_ID_HEADER, id);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(REQUEST_ID_HEADER, id))
                .doFinally(signal -> MDC.remove(REQUEST_ID_HEADER));
    }
}
