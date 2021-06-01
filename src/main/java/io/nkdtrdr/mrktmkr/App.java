package io.nkdtrdr.mrktmkr;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPoolConfig;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
@EnableScheduling
@EnableRedisRepositories
public class App {
    private static final Logger LOGGER = getLogger(App.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = run(App.class, args);

        final ProcessMediator bean = applicationContext.getBean(ProcessMediator.class);
        bean.start();
        LOGGER.info("App be runnin'");
    }

    @Bean
    public BinanceApiClientFactory binanceApiClientFactory(@Value("${binance.api-key}") String apiKey,
                                                           @Value("${binance.api-secret}") String apiSecret) {
        return BinanceApiClientFactory.newInstance(apiKey, apiSecret);
    }

    @Bean
    public BinanceApiAsyncRestClient binanceApiRestClient(BinanceApiClientFactory clientFactory) {
        return clientFactory.newAsyncRestClient();
    }

    @Bean
    public BinanceApiWebSocketClient binanceApiWebSocketClient(BinanceApiClientFactory clientFactory) {
        return clientFactory.newWebSocketClient();
    }

    @Bean
    public BinanceApiRestClient binanceSyncApiRestClient(BinanceApiClientFactory factory) {
        return factory.newRestClient();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(@Value("${spring.redis.host}") String host) {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host);
        final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(configuration);

        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }
}
