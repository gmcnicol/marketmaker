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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class App {
    private static final Logger LOGGER = getLogger(App.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = run(App.class, args);

        final ProcessMediator bean = applicationContext.getBean(ProcessMediator.class);
        bean.start();
        LOGGER.warn("App be runnin'");
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
}
