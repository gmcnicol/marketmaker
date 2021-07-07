package io.nkdtrdr.mrktmkr.strategy.buy;

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
public class BuyStrategy implements KdTradingStrategy {
    public static final BigDecimal SEVENTY = valueOf(70L);
    private static final String STRATEGY_NAME = "BUY";
    private static final BigDecimal TWENTY = getBigDecimal("20");
    private static final BigDecimal EIGHTY = getBigDecimal("80");
    private final Predicate<KdValue> activatePredicate;
    private final Predicate<KdValue> canPlaceOrderPredicate;
    private final Predicate<Order> affordPredicate;
    private final String name;
    private final Map<String, KdValue> previousKdValue = new HashMap<>();
    private final Predicate<KdValue> longSymbolMatches;
    private final Predicate<KdValue> symbolMatches;
    private final Predicate<BigDecimal> pricePredicate;
    private boolean canBeActivated;
    private boolean canPlaceABuy;
    private StrategyMediator mediator;
    private boolean locked;

    public BuyStrategy() {
        name = STRATEGY_NAME;
        Predicate<KdValue> kGreaterThanPreviousK =
                value -> value.getkValue().compareTo(getLastValue(value).getkValue()) >= 0;

        Predicate<KdValue> lessThanTwenty = value ->
                value.getkValue().compareTo(TWENTY) < 0
                        || getLastValue(value).getkValue().compareTo(TWENTY) < 0;

        Predicate<KdValue> kLessThanEighty = kdValue -> kdValue.getkValue().compareTo(EIGHTY) < 0;
        Predicate<KdValue> dLessThanSeventy = kdValue -> kdValue.getdValue().compareTo(SEVENTY) < 0;

        Predicate<KdValue> dLessThanThirty = kdValue -> kdValue.getdValue().compareTo(valueOf(30L)) <= 0;
        symbolMatches = kdValue -> kdValue.getSymbol().equals(mediator.getSymbol()) && kdValue.getInterval().equals(
                "1m");

        longSymbolMatches =
                kdValue -> kdValue.getSymbol().equals(mediator.getSymbol()) && kdValue.getInterval().equals("1m");
        Predicate<KdValue> kGreaterThanD = value -> value.getkValue().compareTo(value.getdValue()) >= 0;

        Predicate<KdValue> previousKWasLessThanPreviousD = this::previousKLessThanPreviousD;

        pricePredicate = price ->
                price.subtract(mediator.getLowPrice())
                        .divide(mediator.getHighPrice().subtract(mediator.getLowPrice()), 2, RoundingMode.HALF_EVEN)
                        .compareTo(valueOf(0.70D)) < 0;

        activatePredicate = kGreaterThanPreviousK
                .and(previousKWasLessThanPreviousD)
                .and(lessThanTwenty)
                .and(dLessThanThirty)
                .and(kGreaterThanD);

        canPlaceOrderPredicate = lessThanTwenty
                .and(kGreaterThanPreviousK)
                .and(kGreaterThanD);

        this.affordPredicate = c -> c.getValue().compareTo(valueOf(10.10D)) > 0;
    }

    @Override
    public void processLatestReading(KdValue value) {
        canBeActivated =
                getLastValue(value) != null && (longSymbolMatches.test(value) && activatePredicate.test(value));
        canPlaceABuy =
                getLastValue(value) != null && (symbolMatches.test(value) && canPlaceOrderPredicate.test(value));
        setLastValue(value);
    }

    private void setLastValue(final KdValue value) {
        previousKdValue.put(value.getInterval(), value);
    }

    private KdValue getLastValue(final KdValue currentValue) {
        return previousKdValue.computeIfAbsent(currentValue.getInterval(), s -> null);
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

        final boolean canAfford = affordPredicate.test(order);
        final boolean priceTest = pricePredicate.test(order.getPrice());
        return STRATEGY_NAME.equals(order.getStrategy())
                && order.getSide().equals(Order.OrderSide.BUY)
                && canPlaceABuy
                && canAfford
                && priceTest;
    }

    @Override
    public Order getInitialOrder() {

        return Order.newBuilder()
                .setOrderTrigger(Order.OrderTrigger.IMMEDIATE)
                .setOrderId(String.format("O%s%s", STRATEGY_NAME, formattedDateString(now())))
                .setStrategy(STRATEGY_NAME)
                .setSide(Order.OrderSide.BUY)
                .setSymbol(mediator.getSymbol())
                .build();
    }

    @Override
    public boolean canTrade() {
        return canPlaceABuy;
    }

    @Override
    public void setMediator(StrategyMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean isLocked() {
        return locked;
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
                .append("canPlaceABuy", canPlaceABuy)
                .toString();
    }

    private boolean previousKLessThanPreviousD(KdValue kdValue) {
        final KdValue lastValue = getLastValue(kdValue);
        return lastValue.getkValue().compareTo(lastValue.getdValue()) < 0;
    }
}
