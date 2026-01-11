package com.ren.lostintime.common.entity.util;

public interface IEggLayer {

    boolean hasEgg();

    void setHasEgg(boolean pHasEgg);

    int getLayEggCounter();

    void setLayEggCounter(int layEggCounter);

    boolean isLayingEgg();

    void setLayingEgg(boolean pIsLayingEgg);

    void onEggLaid();
}
