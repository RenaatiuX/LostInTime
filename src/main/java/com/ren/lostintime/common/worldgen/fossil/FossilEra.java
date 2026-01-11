package com.ren.lostintime.common.worldgen.fossil;

public enum FossilEra {

    CAMBRIAN  (-64, -48, 6),
    ORDOVICIAN(-60, -40, 6),
    SILURIAN  (-56, -32, 6),
    DEVONIAN  (-48, -24, 6),
    CARBONIFEROUS(-40, -16, 6),
    PERMIAN   (-32,   0, 6),
    TRIASSIC  (-24,  16, 6),
    JURASSIC  (-16,  32, 6),
    CRETACEOUS(  0,  48, 6),
    PALEOGENE ( 16,  64, 6),
    NEOGENE   ( 32,  96, 6),
    QUATERNARY( 48, 120, 6);

    public final int minY;
    public final int maxY;
    public final int count;

    FossilEra(int minY, int maxY, int count) {
        this.minY = minY;
        this.maxY = maxY;
        this.count = count;
    }
}

