package io.nkdtrdr.mrktmkr.strategy;

import org.springframework.stereotype.Component;


@Component
public class ActivationTracker {
    private static final int MINIMUM_ACTIVATION_COUNT = 2;
    private String candidateStrategy;
    private int activationCount;

    void setCandidateStrategy(final String candidateStrategy) {
        if (candidateStrategy.equals(this.candidateStrategy))
            activationCount++;
        else activationCount = 0;
        this.candidateStrategy = candidateStrategy;
    }

    public boolean canActivateStrategy(String candidateStrategy) {
        return candidateStrategy.equals(this.candidateStrategy)
                && activationCount >= MINIMUM_ACTIVATION_COUNT;
    }
}
