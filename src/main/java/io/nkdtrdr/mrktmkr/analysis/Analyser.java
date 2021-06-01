package io.nkdtrdr.mrktmkr.analysis;

public interface Analyser<TValue, TResult> {

    TResult analyse(TValue value);
}
