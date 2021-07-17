package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class KdConfig {
    @Bean
    public TradingStrategyRepository<KdValue> kdValueTradingStrategyRepository(
            final List<TradingStrategy<KdValue>> tradingStrategyList) {
        return new TradingStrategyRepository<>(tradingStrategyList);
    }
}
