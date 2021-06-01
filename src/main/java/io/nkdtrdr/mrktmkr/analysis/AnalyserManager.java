package io.nkdtrdr.mrktmkr.analysis;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.stereotype.Component;


/**
 * The Analyser manager.
 */
@Component
public class AnalyserManager {
    /**
     * The Table.
     */
    Table<String, String, KDAnalyser> table = HashBasedTable.create();

    /**
     * Get kd analyser.
     *
     * @param symbol   the symbol
     * @param interval the interval
     * @return the kd analyser
     */
    public KDAnalyser get(String symbol, String interval) {
        if (!table.contains(symbol, interval)) {
            table.put(symbol, interval, new KDAnalyser(14, 3));
        }
        return table.get(symbol, interval);
    }
}
