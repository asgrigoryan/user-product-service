package com.reactive.userorderservice.configuration;

import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

@Configuration
public class ReactorMdcConfiguration {

    @PostConstruct
    public void init() {
        Hooks.onEachOperator("MDC", Operators.liftPublisher((publisher, actualSubscriber) ->
                new CoreSubscriber<Object>() {

                    final CoreSubscriber<? super Object> subscriber = actualSubscriber;

                    @Override
                    public void onSubscribe(org.reactivestreams.Subscription s) {
                        putMdc(subscriber.currentContext());
                        subscriber.onSubscribe(s);
                        clearMdc();
                    }

                    @Override
                    public void onNext(Object t) {
                        putMdc(subscriber.currentContext());
                        subscriber.onNext(t);
                        clearMdc();
                    }

                    @Override
                    public void onError(Throwable t) {
                        putMdc(subscriber.currentContext());
                        subscriber.onError(t);
                        clearMdc();
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }

                    @Override
                    public Context currentContext() {
                        return subscriber.currentContext();
                    }

                    private void putMdc(Context ctx) {
                        String requestId = ctx.getOrDefault("X-Request-Id", null);
                        if (requestId != null) {
                            MDC.put("X-Request-Id", requestId);
                        }
                    }

                    private void clearMdc() {
                        MDC.remove("X-Request-Id");
                    }
                }
        ));
    }
}
