package com.github.iamhi.hizone.authentication.data;

import com.github.iamhi.hizone.authentication.config.RedisConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisClientWrapper implements RedisRepository, RedisPubSubWrapper {

    RedisReactiveCommands<String, String> reactiveCommands;

    RedisPubSubReactiveCommands<String, String> reactivePubSubCommands;

    public RedisClientWrapper(RedisConfig redisConfig) {
        RedisClient redisClient = RedisClient.create("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        reactivePubSubCommands = redisClient.connectPubSub().reactive();
        reactiveCommands = connection.reactive();
    }

    @Override
    public Mono<Void> subscribe(String... channels) {
        return reactivePubSubCommands.subscribe(channels);
    }

    @Override
    public Mono<Void> unsubscribe(String... channels) {
        return reactivePubSubCommands.unsubscribe(channels);
    }

    @Override
    public Mono<Long> publish(String channel, String message) {
        return reactivePubSubCommands.publish(channel, message);
    }

    @Override
    public RedisReactiveCommands<String, String> getReactiveConnection() {
        return reactiveCommands;
    }
}
