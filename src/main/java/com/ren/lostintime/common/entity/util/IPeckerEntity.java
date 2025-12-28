package com.ren.lostintime.common.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IPeckerEntity {

    void setPeckTarget(BlockPos target);
    BlockPos getPeckTarget();

    PeckState getPeckState();

    void setPeckState(PeckState state);

    /**
     * called at the beginning when starting to peg, can trigger animations etc
     */
    void startPecking();

    /**
     * called at the end of pecking when drops should spawn
     */
    void finishPecking();

    boolean canPeck();


    default boolean isFruit(ItemEntity entity){
        return isFruit(entity.getItem());
    }
    boolean isFruit(ItemStack stack);

    /**
     * this will reset the packing state to none which should cancel everything related to pecking what is going on
     */
    default void cancelPecking(){
        setPeckState(PeckState.NONE);
    }

    /**
     * checks if the entity can peck at that position
     * @param pos the peck position
     * @param state the state at that position
     * @param level the level this is all in
     * @return whether the entity can peck there
     */
    default boolean canPeckAt(BlockPos pos, BlockState state, Level level){
        return true;
    }

    boolean isValidSoil(BlockState state);

    static enum PeckState {
        NONE,
        MOVING,
        CIRCLING,
        PECKING
    }
}
