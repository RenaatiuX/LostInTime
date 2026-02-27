package com.ren.lostintime.common.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;

public class NoJumpControl extends JumpControl {
    /**
     * prevents the mob from jumping
     * @param pMob
     */
    public NoJumpControl(Mob pMob) {
        super(pMob);
    }

    @Override
    public void jump() {
        // Do nothing cause we dont want to jump
    }

    @Override
    public void tick() {
        //Do nothing cause we dont want to jump
    }
}
