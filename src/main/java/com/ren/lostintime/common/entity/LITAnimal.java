package com.ren.lostintime.common.entity;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.util.ISleepingEntity;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.init.ParticlesInit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class LITAnimal extends Animal {

    protected LazyOptional<SleepController<?>> sleepControllerOptional;

    //sleep particles
    private int sleepParticleCooldown = 0;

    public LITAnimal(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        var sleepController = getSleepController();
        this.sleepControllerOptional = LazyOptional.of(sleepController == null ? null : () -> sleepController);
    }

    /**
     * Returns the {@link SleepController} for this animal.
     * Subclasses should override this to provide their specific sleep logic.
     *
     * @return the sleep controller, or {@code null} if this animal does not use one.
     */
    @Nullable
    public SleepController<?> getSleepController() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (super.hurt(pSource, pAmount) && !level().isClientSide()) {
            sleepControllerOptional.ifPresent(c -> c.forceWakeUp(200));
            return true;
        }
        return false;
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide()) {
            sleepControllerOptional.ifPresent(SleepController::tick);
        }
        super.aiStep();
        if (this.level().isClientSide() && this.isSleeping()) {
            spawnSleepingParticles();
        }
    }

    protected void spawnSleepingParticles() {
        if (!this.isSleeping()) return;

        if (sleepParticleCooldown > 0) {
            sleepParticleCooldown--;
            return;
        }
        sleepParticleCooldown = 40 + this.random.nextInt(40);

        double x = this.getX();
        double y = this.getY() + this.getBbHeight() + 0.15D;
        double z = this.getZ();
        this.level().addParticle(ParticlesInit.SLEEPING_PARTICLES.get(), x, y, z, 0f, 0.4f, 0);
    }


    @Override
    protected void updateControlFlags() {
        boolean canControlThisEntity = this.getControllingPassenger() instanceof Mob;
        boolean isInsideBoat = this.getVehicle() instanceof Boat;
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !canControlThisEntity && !isSleeping());
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !canControlThisEntity && !isInsideBoat && !isSleeping());
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !canControlThisEntity && !isSleeping());
    }

    public static boolean checkLITAnimalSpawnRules(EntityType<? extends Animal> pAnimal, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return Config.naturalSpawns && pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
    }
}
