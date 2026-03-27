package com.mlbstats.domain.gwar;

import java.math.BigDecimal;

/**
 * Record representing the components of a gWAR calculation.
 * All values are in runs above average/replacement except gwar which is in wins.
 */
public record GwarComponents(
        BigDecimal gwar,
        BigDecimal batting,
        BigDecimal baserunning,
        BigDecimal fielding,
        BigDecimal positional,
        BigDecimal replacement
) {

    /**
     * Creates empty components (all zeros).
     */
    public static GwarComponents empty() {
        return new GwarComponents(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    /**
     * Returns the sum of all run components.
     */
    public BigDecimal totalRuns() {
        return batting.add(baserunning).add(fielding).add(positional).add(replacement);
    }
}
