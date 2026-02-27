package com.ren.lostintime.common.entity.enums;

public enum HylonomusVariant {

    STRIPPED(0),
    LEAF(1),
    ROCK(2),
    RUSTY(3),
    SPOTTED(4),
    STELAR(5);

    private final int id;

    HylonomusVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static HylonomusVariant byId(int id) {
        if (id < 0 || id >= values().length) return STRIPPED;
        return values()[id];
    }
}
