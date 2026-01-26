package com.ren.lostintime.common.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.logging.Level;

public interface IEggLayer {

    /**
     *
     * @return whether this has an egg this must be client side synced
     */
    boolean hasEgg();

    /**
     * should make this entity hatch an egg and make {@link IEggLayer#hasEgg()} return true
     * does nothing when this entity already has an egg
     * @param hatchTicks the amount of ticks this entity needs until it will start searching for a place to place the egg
     */
    void hatchEgg(int hatchTicks);

    boolean validEggPosition(BlockPos pos, BlockState state);

    /**
     * called after the egg was placed and everything was taken care of on the server side
     * @param eggPos the position the egg was placed on
     */
    default void onEggLayed(BlockPos eggPos){}

    /**
     *
     * @return whether this entity can lay the egg meaning it should search for a place for the egg
     */
    boolean canLayEgg();

    BlockState getEgg();

}
