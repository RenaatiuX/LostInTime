package com.ren.lostintime.common.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public interface IEggLayerAnimal {
     boolean canLayEgg(ServerLevel level, Animal entity, BlockPos pos);

     BlockState getEggState(ServerLevel level, Animal entity, BlockPos pos);

     default SoundEvent getEggLaySound(ServerLevel level, Animal entity, BlockPos pos){
          return SoundEvents.FROG_LAY_SPAWN;
     }
}
