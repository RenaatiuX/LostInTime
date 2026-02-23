package com.ren.lostintime.common.block.properties;

import net.minecraft.util.StringRepresentable;

public enum TitanosarcolitesPart implements StringRepresentable {

    PINCER_LEFT("pincer_left"),
    PINCER_RIGHT("pincer_right"),
    BASE_LEFT("base_left"),
    BASE_RIGHT("base_right");

    private final String name;

    TitanosarcolitesPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
