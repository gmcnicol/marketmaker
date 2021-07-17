package io.nkdtrdr.mrktmkr.analysis.utilities;

import io.nkdtrdr.mrktmkr.analysis.model.StochasticPeriod;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.util.Comparator.naturalOrder;


public class StochasticKDCalculators {
    public static BigDecimal getKValue(Collection<StochasticPeriod> values, BigDecimal currentClose) {
        final BigDecimal low = getLowFromValues(values);
        final BigDecimal high = getHighFromValues(values);

        final BigDecimal denominator = high.subtract(low);
        return (denominator.compareTo(ZERO) != 0)
                ? currentClose.subtract(low).divide(high.subtract(low), 8, FLOOR)
                : ZERO;
    }

    private static BigDecimal getLowFromValues(Collection<StochasticPeriod> values) {
        return values.stream()
                .map(StochasticPeriod::getLow)
                .min(naturalOrder())
                .orElse(ZERO);
    }

    private static BigDecimal getHighFromValues(Collection<StochasticPeriod> values) {
        return values.stream()
                .map(StochasticPeriod::getHigh)
                .max(naturalOrder())
                .orElse(ZERO);
    }

    public static BigDecimal getDValue(CircularFifoQueue<BigDecimal> dData, BigDecimal dDivisor) {
        return dData.stream()
                .map(Objects::requireNonNull)
                .reduce(ZERO, BigDecimal::add)
                .divide(dDivisor, 8, FLOOR);
    }
}
