package com.ren.lostintime.common.entity.util;

import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.stream.Collectors;

public enum TimePeriod {

    //PALEOZOIC
    CAMBRIAN(541, 485, "Paleozoic", 0x557A5C, false),
    ORDOVICIAN(485, 443, "Paleozoic", 0x009295, false),
    SILURIAN(443, 419, "Paleozoic", 0xB0608F, false),
    DEVONIAN(419, 358, "Paleozoic", 0xBD8B4D, false),
    CARBONIFEROUS(358, 298, "Paleozoic", 0x3D8B62, false),
    PERMIAN(298, 252, "Paleozoic", 0xF24B31, false),

    //MESOZOIC
    TRIASSIC(252, 201, "Mesozoic", 0x93318E, false),
    JURASSIC(201, 145, "Mesozoic", 0x00AEEF, false),
    CRETACEOUS(145, 66, "Mesozoic", 0x82BC43, false),

    //CENOZOIC
    PALEOGENE(66, 23, "Cenozoic", 0xFFAA00, false),
    NEOGENE(23, 2.6, "Cenozoic", 0xFFE500, false),
    QUATERNARY(2.6, 0, "Cenozoic", 0xF7EEAD, false),

    //EON
    PHANEROZOIC(541, 0, "None", 0xFFFFFF, true);

    /**
     * Years ago
     */
    public final double fromMa;
    public final double toMa;
    public final String era;
    public final int color;
    public final boolean isEon;
    public final String descriptionKey;

    TimePeriod(double from, double to, String era, int color, boolean isEon) {
        this.fromMa = from;
        this.toMa = to;
        this.era = era;
        this.color = color;
        this.isEon = isEon;
        this.descriptionKey = "lostintime.timeperiod." + this.name().toLowerCase() + ".desc";
    }

    /**
     * Returns all registered entities that belong to this period.
     */
    public List<EntityType<?>> getNotableCreatures() {
        return EntityInit.ENTITIES.getEntries().stream()
                .map(RegistryObject::get)
                .filter(type -> {
                    //return isEntityFromPeriod(type, this);
                    return true;
                })
                .collect(Collectors.toList());
    }
}
