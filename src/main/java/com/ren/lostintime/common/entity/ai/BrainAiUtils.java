package com.ren.lostintime.common.entity.ai;

import com.ren.lostintime.common.entity.util.IEggLayerAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.mutable.MutableLong;

public class BrainAiUtils {
    public static <E extends Animal & IEggLayerAnimal> OneShot<E> layEggWhenPossible() {
        return BehaviorBuilder.create((context) -> {
            return context.group(context.registered(MemoryModuleType.IS_PREGNANT)).apply(context, pregnant ->
                    (level, entity, gameTime) -> {
                        if (entity.isInWater() && !entity.onGround()) {
                            BlockPos blockpos = entity.blockPosition().below();

                            for (Direction direction : Direction.values()) {
                                BlockPos directionRelative = blockpos.relative(direction);
                                if (entity.canLayEgg(level, entity, directionRelative)) {
                                    BlockState blockstate = entity.getEggState(level, entity, directionRelative);
                                    level.setBlock(directionRelative, blockstate, 3);
                                    level.gameEvent(GameEvent.BLOCK_PLACE, directionRelative, GameEvent.Context.of(entity, blockstate));
                                    level.playSound((Player) null, entity, entity.getEggLaySound(level, entity, directionRelative), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    pregnant.erase();
                                    return true;
                                }
                            }
                        }

                        return false;
                    });
        });
    }

    public static BehaviorControl<PathfinderMob> forceFindWater(int range, float pSpeedModifier) {
        MutableLong mutablelong = new MutableLong(0L);
        return BehaviorBuilder.create((p_260101_) -> {
            return p_260101_.group(p_260101_.absent(MemoryModuleType.WALK_TARGET), p_260101_.registered(MemoryModuleType.LOOK_TARGET)).apply(p_260101_, (p_259692_, p_259819_) -> {
                return (p_260228_, p_259212_, p_260041_) -> {
                    if (p_260228_.getFluidState(p_259212_.blockPosition()).is(FluidTags.WATER)) {
                        return false;
                    } else if (p_260041_ < mutablelong.getValue()) {
                        mutablelong.setValue(p_260041_ + 20L + 2L);
                        return true;
                    } else {
                        BlockPos blockpos = null;
                        BlockPos blockpos1 = null;
                        BlockPos blockpos2 = p_259212_.blockPosition();

                        for (BlockPos blockpos3 : BlockPos.withinManhattan(blockpos2, range, range, range)) {
                            if (blockpos3.getX() != blockpos2.getX() || blockpos3.getZ() != blockpos2.getZ()) {
                                BlockState blockstate = p_259212_.level().getBlockState(blockpos3.above());
                                BlockState blockstate1 = p_259212_.level().getBlockState(blockpos3);
                                if (blockstate1.is(Blocks.WATER)) {
                                    if (blockstate.isAir()) {
                                        blockpos = blockpos3.immutable();
                                        break;
                                    }

                                    if (blockpos1 == null && !blockpos3.closerToCenterThan(p_259212_.position(), 1.5D)) {
                                        blockpos1 = blockpos3.immutable();
                                    }
                                }
                            }
                        }

                        if (blockpos == null) {
                            blockpos = blockpos1;
                        }

                        if (blockpos != null) {
                            p_259819_.set(new BlockPosTracker(blockpos));
                            p_259692_.set(new WalkTarget(new BlockPosTracker(blockpos), pSpeedModifier, 0));
                        }

                        mutablelong.setValue(p_260041_ + 40L);
                        return true;
                    }
                };
            });
        });
    }
}
