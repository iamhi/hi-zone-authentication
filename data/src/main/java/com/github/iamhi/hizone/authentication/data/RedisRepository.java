package com.github.iamhi.hizone.authentication.data;

import io.lettuce.core.api.reactive.RedisReactiveCommands;

public interface RedisRepository {

    RedisReactiveCommands<String, String> getReactiveConnection();
}
