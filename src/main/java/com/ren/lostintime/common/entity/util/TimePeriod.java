package com.ren.lostintime.common.entity.util;

import java.util.Locale;

public enum TimePeriod {

    TRIASSIC(
            252_000_000,
            201_000_000,
            "lostintime.timeperiod.triassic.desc"
    ),
    JURASSIC(
            201_000_000,
            145_000_000,
            "lostintime.timeperiod.jurassic.desc"
    ),
    CRETACEOUS(
            145_000_000,
            66_000_000,
            "lostintime.timeperiod.cretaceous.desc"
    ),
    PALEOGENE(
            66_000_000,
            23_000_000,
            "lostintime.timeperiod.paleogene.desc"
    ),
    NEOGENE(
            23_000_000,
            2_600_000,
            "lostintime.timeperiod.neogene.desc"
    ),
    QUATERNARY(
            2_600_000,
            0,
            "lostintime.timeperiod.quaternary.desc"
    );

    /** Years ago */
    public final double fromYear;
    public final double toYear;

    public final String descriptionKey;

    TimePeriod(double fromYear, double toYear, String descriptionKey) {
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.descriptionKey = descriptionKey;
    }
}
