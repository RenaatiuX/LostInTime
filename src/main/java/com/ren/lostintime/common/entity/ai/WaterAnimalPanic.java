package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class WaterAnimalPanic<E extends PathfinderMob> extends Behavior<E> {

    private static final Predicate<PathfinderMob> DEFAULT_SHOULD_PANIC_PREDICATE = (p_289313_) -> {
        return p_289313_.getLastHurtByMob() != null || p_289313_.isFreezing() || p_289313_.isOnFire();
    };
    private final float speedMultiplier;
    private final Predicate<E> shouldPanic;
    private final Function<E, Vec3> panicPosGetter;

    public WaterAnimalPanic(float speedModifier) {
        this(speedModifier, DEFAULT_SHOULD_PANIC_PREDICATE::test, WaterAnimalPanic::getPanicPos);
    }

    public WaterAnimalPanic(float speedModifier, Predicate<E> shouldPanic) {
        this(speedModifier, shouldPanic, WaterAnimalPanic::getPanicPos);
    }

    public WaterAnimalPanic(float speedModifier, Function<E, Vec3> panicPosGetter) {
        this(speedModifier, DEFAULT_SHOULD_PANIC_PREDICATE::test, panicPosGetter);
    }

    public WaterAnimalPanic(float speedModifier, Predicate<E> shouldPanic, Function<E, Vec3> panicPosGetter) {
        super(ImmutableMap.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryStatus.VALUE_PRESENT), 100, 120);
        this.speedMultiplier = speedModifier;
        this.shouldPanic = shouldPanic;
        this.panicPosGetter = panicPosGetter;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, E pOwner) {
        if (pOwner.getBrain().getMemory(MemoryModuleType.HURT_BY).map(d -> d.is(DamageTypeTags.IS_DROWNING)).orElse(false))
            return false;
        return this.shouldPanic.test(pOwner);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, E pEntity, long pGameTime) {
        return true;
    }

    @Override
    protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
        pEntity.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void stop(ServerLevel pLevel, E pEntity, long pGameTime) {
        Brain<?> brain = pEntity.getBrain();
        brain.eraseMemory(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void tick(ServerLevel pLevel, E pOwner, long pGameTime) {
        if (pOwner.getNavigation().isDone()) {
            Vec3 vec3 = panicPosGetter.apply(pOwner);
            if (vec3 != null) {
                pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedMultiplier, 0));
            }
        }

    }

    @Nullable
    private static Vec3 getPanicPos(PathfinderMob pPathfinder) {
        if (pPathfinder.isOnFire()) {
            Optional<Vec3> optional = lookForWater(pPathfinder.level(), pPathfinder).map(Vec3::atBottomCenterOf);
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return LandRandomPos.getPos(pPathfinder, 5, 4);
    }

    private static Optional<BlockPos> lookForWater(BlockGetter pLevel, Entity pEntity) {
        BlockPos blockpos = pEntity.blockPosition();
        if (!pLevel.getBlockState(blockpos).getCollisionShape(pLevel, blockpos).isEmpty()) {
            return Optional.empty();
        } else {
            Predicate<BlockPos> predicate;
            if (Mth.ceil(pEntity.getBbWidth()) == 2) {
                predicate = (p_284705_) -> {
                    return BlockPos.squareOutSouthEast(p_284705_).allMatch((p_196646_) -> {
                        return pLevel.getFluidState(p_196646_).is(FluidTags.WATER);
                    });
                };
            } else {
                predicate = (p_284707_) -> {
                    return pLevel.getFluidState(p_284707_).is(FluidTags.WATER);
                };
            }

            return BlockPos.findClosestMatch(blockpos, 5, 1, predicate);
        }
    }


}
