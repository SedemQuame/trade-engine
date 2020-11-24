package com.trade.utility;

import redis.clients.jedis.Jedis;
import java.net.URI;
import java.net.URISyntaxException;

public class JedisConnection {
    private static final int PORT = 8090;

    public Jedis getConnection() throws URISyntaxException {
        URI redisURI = null;
        if (System.getenv("REDIS_URL") != null) {
            redisURI = new URI(System.getenv("REDIS_URL"));
        } else {
            redisURI = new URI("http://localhost:" + PORT);
        }
        return (new Jedis(redisURI));
    }
}
