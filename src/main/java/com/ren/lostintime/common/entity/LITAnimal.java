package com.ren.lostintime.common.entity;

import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.init.ParticlesInit;
import com.ren.lostintime.common.item.IScannable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class LITAnimal extends Animal implements IScannable {

    // ==========================================
    // DATA ACCESSORS & VARIABLES
    // ==========================================
    protected static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> CURRENT_HUNGER = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_MALE = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_PREGNANT = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> PREGNANCY_PROGRESS = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> GROWTH_STAGE = SynchedEntityData.defineId(LITAnimal.class, EntityDataSerializers.INT);

    protected LazyOptional<SleepController<?>> sleepControllerOptional;
    private BlockPos jukebox;

    private int sleepParticleCooldown = 0;
    private int hungerTickTimer = 0;

    // ==========================================
    // INITIALIZATION AND NBT
    // ==========================================
    public LITAnimal(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        var sleepController = getSleepController();
        this.sleepControllerOptional = LazyOptional.of(sleepController == null ? null : () -> sleepController);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SLEEPING, false);
        this.entityData.define(CURRENT_HUNGER, this.getBaseMaxHunger());
        this.entityData.define(VARIANT, 0);
        this.entityData.define(DANCING, false);
        this.entityData.define(IS_MALE, this.random.nextBoolean());
        this.entityData.define(IS_PREGNANT, false);
        this.entityData.define(PREGNANCY_PROGRESS, 0);
        this.entityData.define(GROWTH_STAGE, 2);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsSleeping", this.isSleeping());
        pCompound.putFloat("CurrentHunger", this.getHunger());
        pCompound.putInt("Variant", this.getVariant());
        pCompound.putBoolean("IsDancing", this.isDancing());
        pCompound.putBoolean("IsMale", this.isMale());
        pCompound.putBoolean("IsPregnant", this.isPregnant());
        pCompound.putInt("PregnancyProgress", this.getPregnantProcess());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setSleeping(pCompound.getBoolean("IsSleeping"));
        this.setHunger(pCompound.getFloat("CurrentHunger"));
        this.setVariant(pCompound.getInt("Variant"));
        this.setDancing(pCompound.getBoolean("IsDancing"));
        this.setMale(pCompound.getBoolean("IsMale"));
        this.setPregnant(pCompound.getBoolean("IsPregnant"));
        this.setPregnantProcess(pCompound.getInt("PregnancyProgress"));
    }

    // ==========================================
    // ISCANNABLE INTERFACE
    // ==========================================
    @Override
    public String getGenderName() {
        return this.isMale() ? "Male" : "Female";
    }

    @Override
    public int getGestationTicks() {
        return this.getPregnancyDuration() - this.getPregnantProcess();
    }

    public String getGrowthStageName() {
        GrowthStage stage = this.getGrowthStage();
        if (stage == GrowthStage.BABY) return "Baby";
        if (stage == GrowthStage.JUVENILE) return "Young";
        return "Adult";
    }

    public int getTicksUntilNextStage() {
        int age = this.getAge();
        if (age >= 0) return 0;

        if (this.hasJuvenileStage()) {
            int juvThreshold = this.getJuvenileAgeThreshold();
            if (age <= juvThreshold) {
                return Math.abs(age) - Math.abs(juvThreshold);
            }
        }
        return Math.abs(age);
    }

    // ==========================================
    // DYNAMIC HUNGER SYSTEM
    // ==========================================
    public float getBaseMaxHunger() {
        return 100F;
    }

    public float getHungerThreshold() {
        return 0.5F;
    }

    public int getHungerTickInterval() {
        return 120;
    }

    public float getMaxHunger() {
        float base = this.getBaseMaxHunger();
        GrowthStage stage = this.getGrowthStage();
        if (stage == GrowthStage.BABY) return base * 0.3F;
        if (stage == GrowthStage.JUVENILE) return base * 0.65F;
        return base;
    }

    public float getHunger() {
        return this.entityData.get(CURRENT_HUNGER);
    }

    public void setHunger(float hunger) {
        this.entityData.set(CURRENT_HUNGER, Mth.clamp(hunger, 0, this.getMaxHunger()));
    }

    public boolean isHungry() {
        return this.getHunger() <= this.getMaxHunger() * this.getHungerThreshold();
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isFood(itemstack)) {
            if (this.getHunger() < this.getMaxHunger() || this.getGrowthStage() != GrowthStage.ADULT) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                float recovery = this.getMaxHunger() * 0.25F;
                this.setHunger(this.getHunger() + recovery);

                if (this.isBaby()) {
                    this.ageUp((int)((float)(-this.getAge() / 20) * 0.1F), true);
                }

                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte)18);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    // ==========================================
    // GROWTH SYSTEM
    // ==========================================
    public boolean hasJuvenileStage() {
        return false;
    }

    public int getGrowthTicks() {
        return 24000;
    }

    public int getJuvenileAgeThreshold() {
        return -(this.getGrowthTicks() / 2);
    }

    protected void updateSyncedGrowthStage() {
        if (this.level().isClientSide) return;
        int age = this.getAge();
        int currentStage = 2;

        if (age < 0) {
            if (this.hasJuvenileStage() && age > this.getJuvenileAgeThreshold()) {
                currentStage = 1;
            } else {
                currentStage = 0;
            }
        }
        if (this.entityData.get(GROWTH_STAGE) != currentStage) {
            this.entityData.set(GROWTH_STAGE, currentStage);
            this.setHunger(this.getHunger());
        }
    }

    public GrowthStage getGrowthStage() {
        int stage = this.entityData.get(GROWTH_STAGE);
        if (stage == 0) return GrowthStage.BABY;
        if (stage == 1) return GrowthStage.JUVENILE;
        return GrowthStage.ADULT;
    }

    @Override
    public void setAge(int pAge) {
        super.setAge(pAge);
        this.updateSyncedGrowthStage();
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions baseDimensions = super.getDimensions(pPose);
        if (this.hasJuvenileStage()) {
            GrowthStage stage = this.getGrowthStage();
            if (stage == GrowthStage.BABY) return baseDimensions.scale(0.3F);
            if (stage == GrowthStage.JUVENILE) return baseDimensions.scale(0.65F);
        }
        return this.isBaby() ? baseDimensions.scale(0.5F) : baseDimensions;
    }

    // ==========================================
    // REPRODUCTIVE SYSTEM AND GENDER
    // ==========================================
    public int getPregnancyDuration() {
        return 6000;
    }

    public int getMatingCooldownTicks() {
        return 6000;
    }

    public boolean isMale() {
        return this.entityData.get(IS_MALE);
    }

    public void setMale(boolean male) {
        this.entityData.set(IS_MALE, male);
    }

    public boolean isPregnant() {
        return this.entityData.get(IS_PREGNANT);
    }

    public void setPregnant(boolean pregnant) {
        this.entityData.set(IS_PREGNANT, pregnant);
    }

    public int getPregnantProcess() {
        return this.entityData.get(PREGNANCY_PROGRESS);
    }

    public void setPregnantProcess(int progress) {
        this.entityData.set(PREGNANCY_PROGRESS, progress);
    }

    @Override
    public boolean canMate(@NotNull Animal pOtherAnimal) {
        if (pOtherAnimal == this || pOtherAnimal.getClass() != this.getClass()) return false;

        LITAnimal mate = (LITAnimal) pOtherAnimal;
        if (this.isPregnant() || mate.isPregnant()) return false;

        return (this.isMale() != mate.isMale()) && this.isInLove() && mate.isInLove();
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        LITAnimal mate = (LITAnimal) pMate;

        ServerPlayer serverplayer = this.getLoveCause() != null ? this.getLoveCause() : pMate.getLoveCause();
        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this, pMate, null);
        }

        LITAnimal mother = this.isMale() ? mate : this;
        LITAnimal father = this.isMale() ? this : mate;

        mother.setPregnant(true);
        mother.setPregnantProcess(0);

        father.setAge(this.getMatingCooldownTicks());

        this.resetLove();
        pMate.resetLove();

        pLevel.broadcastEntityEvent(this, (byte) 18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    public void giveBirth() {
        ServerLevel serverLevel = (ServerLevel) this.level();
        AgeableMob baby = this.getBreedOffspring(serverLevel, this);

        if (baby != null) {
            BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, this, baby);
            MinecraftForge.EVENT_BUS.post(event);

            if (!event.isCancelable()) {
                AgeableMob finalBaby = event.getChild();
                baby.setBaby(true);
                baby.setAge(-this.getGrowthTicks());

                if (finalBaby instanceof LITAnimal litBaby) {
                    litBaby.setMale(this.random.nextBoolean());
                }

                baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
                serverLevel.addFreshEntityWithPassengers(baby);
                this.level().broadcastEntityEvent(this, (byte) 18);
            }
        }

        this.setPregnant(false);
        this.setPregnantProcess(0);
        this.setAge(this.getMatingCooldownTicks());
    }

    // ==========================================
    // MAIN CYCLE (SLEEP, HUNGER, DANCE)
    // ==========================================
    @Override
    public void aiStep() {
        if (!level().isClientSide()) {
            sleepControllerOptional.ifPresent(SleepController::tick);

            this.hungerTickTimer++;
            if (this.hungerTickTimer >= this.getHungerTickInterval()) {
                this.setHunger(this.getHunger() - 1);
                this.hungerTickTimer = 0;
            }

            if (this.getHunger() <= 0 && this.tickCount % 80 == 0) {
                this.hurt(this.damageSources().starve(), 1.0F);
            }

            if (this.isDancing()) {
                if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 5.0D) || this.isSleeping()) {
                    this.setDancing(false);
                    this.jukebox = null;
                }
            }

            if (this.isPregnant()) {
                int currentTimer = this.getPregnantProcess() + 1;
                if (currentTimer % 20 == 0) {
                    this.setPregnantProcess(currentTimer);
                } else {
                    this.entityData.set(PREGNANCY_PROGRESS, currentTimer);
                }

                if (currentTimer >= this.getPregnancyDuration()) {
                    this.giveBirth();
                    this.setPregnantProcess(0);
                }
            }
        }

        super.aiStep();

        if (this.level().isClientSide() && this.isSleeping()) {
            spawnSleepingParticles();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.updateSyncedGrowthStage();
        }
    }

    // ==========================================
    // MISC AND CONTROL FLAGS
    // ==========================================
    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getMaxVariants() {
        return 1;
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DANCING, dancing);
    }

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

    public boolean isSleeping() {
        return this.entityData.hasItem(IS_SLEEPING) ? this.entityData.get(IS_SLEEPING) : super.isSleeping();
    }

    public void setSleeping(boolean sleeping) {
        if (this.entityData.hasItem(IS_SLEEPING)) {
            this.entityData.set(IS_SLEEPING, sleeping);
        }
    }

    @Override
    public void setRecordPlayingNearby(BlockPos pJukebox, boolean pIsPartying) {
        this.jukebox = pJukebox;
        this.setDancing(pIsPartying);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData, @org.jetbrains.annotations.Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        if (this.isBaby()) {
            this.setAge(-this.getGrowthTicks());
        }

        int maxVariants = this.getMaxVariants();
        this.setVariant(maxVariants > 1 ? this.random.nextInt(maxVariants) : 0);

        this.setHunger(this.getMaxHunger());

        return data;
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
