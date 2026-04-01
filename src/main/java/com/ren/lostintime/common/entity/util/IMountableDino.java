package com.ren.lostintime.common.entity.util;

import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;

public interface IMountableDino {

    boolean isSaddleable();

    boolean hasSaddle();

    void equipSaddle(@Nullable SoundSource pSource);

    int getMaxPassengers();

    float getPassengerYOffset();

}
