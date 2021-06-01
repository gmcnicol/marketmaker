package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.limits.Limit;
import io.nkdtrdr.mrktmkr.limits.LimitsRepository;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import io.nkdtrdr.mrktmkr.triggers.TriggersFacade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import static io.nkdtrdr.mrktmkr.utilities.OrderCalculations.aCalculator;


@Component
public class StrategyMediator {
    private static final BigDecimal PENNY = BigDecimal.ONE.scaleByPowerOfTen(-2);
    public final TriggersFacade triggersFacade;
    private final TradingStrategyRepository<KdValue> tradingStrategyRepository;
    private final KdForwardingTradingStrategy activeTradingStrategy;
    private final OrdersFacade ordersFacade;
    private final AccountFacade accountFacade;
    private final Symbol symbol;
    private final LimitsRepository limitsRepository;

    public StrategyMediator(final TradingStrategyRepository<KdValue> tradingStrategyRepository,
                            final OrdersFacade ordersFacade,
                            final AccountFacade accountFacade,
                            final TriggersFacade triggersFacade,
                            final Symbol symbol,
                            final LimitsRepository limitsRepository) {
        this.symbol = symbol;
        this.tradingStrategyRepository = tradingStrategyRepository;
        this.limitsRepository = limitsRepository;
        tradingStrategyRepository.all().forEach(ts -> ts.setMediator(this));
        this.ordersFacade = ordersFacade;
        this.accountFacade = accountFacade;
        this.triggersFacade = triggersFacade;
        this.activeTradingStrategy = new KdForwardingTradingStrategy();
    }

    public void setActiveTradingStrategy(String strategyName) {
        if (activeTradingStrategy.getName().equals(strategyName)) {
            return;
        }
        this.activeTradingStrategy.setInnerStrategy((KdTradingStrategy) tradingStrategyRepository.strategyByName(
                strategyName));
    }

    public void placeInitialOrder() {
        Order initialOrder = activeTradingStrategy.getInitialOrder();
        if (initialOrder.getSide().equals(Order.OrderSide.BUY)) {
            final Limit limitForAssetAndStrategy =
                    limitsRepository.getLimitForAssetAndStrategy(symbol.getQuoteSymbol(), initialOrder.getStrategy());

            final BigDecimal gbp =
                    limitForAssetAndStrategy == null
                            ? accountFacade.getFreeBalanceForAsset(symbol.getQuoteSymbol())
                            : limitForAssetAndStrategy.getAssetCap();

            final BigDecimal price = ordersFacade.getBestAskPrice().add(PENNY);
            final BigDecimal quantity = gbp.divide(price, 6, RoundingMode.FLOOR);
            initialOrder.setPrice(price);
            initialOrder.setQuantity(quantity);
        }

        if (initialOrder.getSide().equals(Order.OrderSide.SELL)) {
            final Limit limitForAssetAndStrategy =
                    limitsRepository.getLimitForAssetAndStrategy(symbol.getBaseSymbol(), initialOrder.getStrategy());

            final BigDecimal maxQuantity = aCalculator()
                    .withPrice(ordersFacade.getBestBidPrice().subtract(PENNY))
                    .withCost(AccountFacade.VALUE_CAP)
                    .withRounding(RoundingMode.CEILING)
                    .getQuantity();

            final BigDecimal quantity = limitForAssetAndStrategy == null
                    ? getFreeBalanceForAsset(symbol.getBaseSymbol()).min(maxQuantity)
                    : limitForAssetAndStrategy.getAssetCap();
            final BigDecimal price = ordersFacade.getBestBidPrice().subtract(PENNY);
            initialOrder.setQuantity(quantity);
            initialOrder.setPrice(price);
        }
        placeOrder(initialOrder);
    }

    public void placeOrder(final Order order) {
        final boolean canPlaceOrder = this.activeTradingStrategy.canPlaceOrder(order);

        if (canPlaceOrder && !ordersFacade.strategyHasTrigger(order.getStrategy()))
            ordersFacade.placeOrder(order);
    }

    public boolean canTrade() {
        return activeTradingStrategy.canTrade();
    }

    public BigDecimal getFreeBalanceForAsset(String asset) {
        return accountFacade.getFreeBalanceForAsset(asset);
    }

    public Set<TradingStrategy<KdValue>> getAllTradingStrategies() {
        return tradingStrategyRepository.all();
    }

    public String getBaseSymbol() {
        return symbol.getBaseSymbol();
    }

    public String getQuoteSymbol() {
        return symbol.getQuoteSymbol();
    }

    public String getSymbol() {
        return symbol.getSymbol();
    }
}
