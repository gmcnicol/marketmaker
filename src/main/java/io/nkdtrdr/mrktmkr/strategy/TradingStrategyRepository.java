package io.nkdtrdr.mrktmkr.strategy;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class TradingStrategyRepository<T> {

    private final ConcurrentMap<String, TradingStrategy<T>> strategyMap;

    public TradingStrategyRepository(List<TradingStrategy<T>> tradingStrategyList) {
        this.strategyMap = tradingStrategyList.stream().collect(Collectors.toConcurrentMap(
                TradingStrategy::getName,
                Function.identity()));
    }

    public TradingStrategy<T> strategyByName(String strategyName) {
        return strategyMap.get(strategyName);
    }

    public Set<TradingStrategy<T>> all() {
        return new HashSet<>(strategyMap.values());
    }
}
