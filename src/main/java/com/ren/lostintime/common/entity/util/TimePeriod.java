package com.ren.lostintime.common.entity.util;

public enum TimePeriod {

    Quaternary(
            2_600_000,
            0,
            "The Quaternary Period began about 2.6 million years ago and continues today. " +
                    "It is marked by repeated ice ages, major climate changes, and the evolution and spread of humans. " +
                    "This period includes the Pleistocene, known for glaciers and early humans, and the Holocene, " +
                    "which features warmer climates and the rise of agriculture and modern civilization."
    );

    /** Years ago */
    public final double fromYear;
    public final double toYear;

    public final String shortDescription;

    TimePeriod(double fromYear, double toYear, String shortDescription) {
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.shortDescription = shortDescription;
    }
}
