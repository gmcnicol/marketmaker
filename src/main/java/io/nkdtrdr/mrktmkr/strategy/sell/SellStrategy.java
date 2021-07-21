package io.nkdtrdr.mrktmkr.strategy.sell;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.strategy.KdTradingStrategy;
import io.nkdtrdr.mrktmkr.strategy.StrategyMediator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static io.nkdtrdr.mrktmkr.utilities.BigDecimalUtilities.getBigDecimal;
import static io.nkdtrdr.mrktmkr.utilities.DateUtils.formattedDateString;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.now;


@Component
public class SellStrategy implements KdTradingStrategy {
    public static final BigDecimal THIRTY = valueOf(30L);
    private static final BigDecimal EIGHTY = getBigDecimal("80");
    private static final BigDecimal TWENTY = getBigDecimal("20");
    private static final String STRATEGY_NAME = "SELL";
    private final Predicate<KdValue> activatePredicate;
    private final Predicate<KdValue> canPlaceOrderPredicate;
    private final Predicate<Order> canAffordPredicate;
    private final String name;
    private final Predicate<KdValue> longSymbolMatches;
    private final Map<String, KdValue> previousKdValue = new HashMap<>();
    private final Predicate<KdValue> shortTermSymbolMatches;
    private final Predicate<BigDecimal> pricePredicate;
    private boolean canBeActivated;
    private boolean canPlaceASell;
    private StrategyMediator mediator;
    private boolean locked;

    public SellStrategy() {
        name = STRATEGY_NAME;

        Predicate<KdValue> kLessThanPreviousK = value -> value.getkValue().compareTo(getLastValue(value).getkValue())
                <= 0;

        Predicate<KdValue> dOverSeventy = kdValue -> kdValue.getdValue().compareTo(valueOf(70L)) > 0;
        Predicate<KdValue> greaterThanEighty = value ->
                value.getkValue().compareTo(EIGHTY) >= 0
                        || getLastValue(value).getkValue().compareTo(EIGHTY) >= 0;

        shortTermSymbolMatches =
                kdValue -> kdValue.getSymbol().equals(mediator.getSymbol()) && kdValue.getInterval().equals(
                        "1m");

        longSymbolMatches =
                kdValue -> kdValue.getSymbol().equals(mediator.getSymbol()) && kdValue.getInterval().equals("1m");

        Predicate<KdValue> lastKGreaterThanLastD = this::previousKGreaterThanPreviousD;

        final Predicate<KdValue> kLessThanD = kdValue -> kdValue.getkValue().compareTo(kdValue.getdValue()) <= 0;

        activatePredicate = kLessThanD
                .and(kLessThanPreviousK)
                .and(lastKGreaterThanLastD)
                .and(greaterThanEighty)
                .and(dOverSeventy);

        final Predicate<KdValue> kGreaterThanTwenty = kdValue -> kdValue.getkValue().compareTo(TWENTY) > 0;
        final Predicate<KdValue> dGreaterThanThirty = kdValue -> kdValue.getkValue().compareTo(THIRTY) > 0;

        canPlaceOrderPredicate = greaterThanEighty
                .and(kLessThanPreviousK)
                .and(kLessThanD)
        ;

        pricePredicate = price ->
                price.subtract(mediator.getLowPrice())
                        .divide(mediator.getHighPrice().subtract(mediator.getLowPrice()), 2, RoundingMode.HALF_EVEN)
                        .compareTo(valueOf(0.5)) > 0;
        this.canAffordPredicate = o -> o.getValue().compareTo(valueOf(10.10D)) >= 0;
    }

    @Override
    public void processLatestReading(KdValue value) {
        canBeActivated = getLastValue(value) != null && longSymbolMatches.test(value) && activatePredicate.test(value);
        canPlaceASell =
                getLastValue(value) != null && shortTermSymbolMatches.test(value) && canPlaceOrderPredicate.test(value);

        setLastValue(value);
    }

    @Override
    public boolean canBeActivated() {
        return this.canBeActivated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canPlaceOrder(final Order order) {
        final boolean canAfford = canAffordPredicate.test(order);
        return STRATEGY_NAME.equals(order.getStrategy())
                && order.getSide().equals(Order.OrderSide.SELL) && canPlaceASell && canAfford
                && pricePredicate.test(order.getPrice())
                ;
    }

    @Override
    public Order getInitialOrder() {
        return Order.newBuilder()
                .setOrderTrigger(Order.OrderTrigger.IMMEDIATE)
                .setOrderId(String.format("O%s%s", STRATEGY_NAME, formattedDateString(now())))
                .setStrategy(STRATEGY_NAME)
                .setSide(Order.OrderSide.SELL)
                .setSymbol(mediator.getSymbol())
                .build();
    }

    @Override
    public boolean canTrade() {
        return canPlaceASell;
    }

    @Override
    public void setMediator(StrategyMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("canBeActivated", canBeActivated)
                .append("canPlaceASell", canPlaceASell)
                .toString();
    }

    private void setLastValue(final KdValue value) {
        previousKdValue.put(value.getInterval(), value);
    }

    private KdValue getLastValue(final KdValue currentValue) {
        return previousKdValue.computeIfAbsent(currentValue.getInterval(), s -> null);
    }

    private boolean previousKGreaterThanPreviousD(KdValue currentValue) {
        final KdValue lastValue = getLastValue(currentValue);
        return lastValue.getkValue().compareTo(lastValue.getdValue()) > 0;
    }
}
