package io.nkdtrdr.mrktmkr.analysis;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.analysis.model.StochasticPeriod;
import io.nkdtrdr.mrktmkr.dto.CandleStickDTO;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.nkdtrdr.mrktmkr.analysis.utilities.StochasticKDCalculators.getDValue;
import static io.nkdtrdr.mrktmkr.analysis.utilities.StochasticKDCalculators.getKValue;


public class KDAnalyser implements Analyser<CandleStickDTO, KdValue> {
    private final CircularFifoQueue<StochasticPeriod> kData;
    private final CircularFifoQueue<BigDecimal> dData;
    private final CircularFifoQueue<KdValue> readings;
    private final BigDecimal dDivisor;

    public KDAnalyser(int kPeriods, int dPeriods) {
        this.kData = new CircularFifoQueue<>(kPeriods);
        this.dData = new CircularFifoQueue<>(dPeriods);
        this.readings = new CircularFifoQueue<>(kPeriods);
        this.dDivisor = new BigDecimal(dPeriods).setScale(8, RoundingMode.HALF_UP);
    }

    public void initialise(List<CandleStickDTO> candlesticks) {
        candlesticks.stream().sorted(Comparator.comparingLong(CandleStickDTO::getOpenTime))
                .map(StochasticPeriod::new)
                .forEach(this::calculate);
    }

    private KdValue calculate(StochasticPeriod period) {
        kData.add(period);
        final BigDecimal kValue = getKValue(this.kData, period.getClose());

        dData.add(kValue);
        final BigDecimal dValue = getDValue(dData, dDivisor);

        final KdValue reading = new KdValue(kValue, dValue, period.getSymbol(), period.getInterval());
        readings.add(reading);
        return reading;
    }

    @Override
    public KdValue analyse(CandleStickDTO candleStickDTO) {
        return calculate(new StochasticPeriod(candleStickDTO));
    }

    public List<KdValue> getReadings() {
        return readings.stream().sorted(Comparator.comparingLong(KdValue::getTimestamp))
                .collect(Collectors.toList());
    }
}
