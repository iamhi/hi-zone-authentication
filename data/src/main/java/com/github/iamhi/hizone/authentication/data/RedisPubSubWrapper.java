package com.github.iamhi.hizone.authentication.data;

import reactor.core.publisher.Mono;

public interface RedisPubSubWrapper {

    Mono<Void> subscribe(String ...channels);

    Mono<Void> unsubscribe(String ...channels);

    Mono<Long> publish(String channel, String message);
}
