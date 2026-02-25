package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.AbstractBaseFish;
import com.ren.lostintime.common.init.AttributeInit;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.LevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class Anomalocaris extends AbstractBaseFish implements GeoEntity {

    // --- Constants ---
    private static final float HUNGER_THRESHOLD = 70.0F;
    private static final float PREY_HEIGHT_MAX = 0.5F;
    private static final float HUNGER_DECREASE_PER_TICK = 1.0F / 400.0F;
    private static final float HUNGER_GAIN_PER_PREY = 30.0F;

    // --- Data Parameters ---
    private static final EntityDataAccessor<Float> DATA_HUNGER = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> DATA_HELD_ITEM = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<UUID>> DATA_CURIO_PLAYER = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_GRABBING = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.BOOLEAN);

    // --- Animations ---
    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    protected static final RawAnimation OUT_OF_WATER = RawAnimation.begin().thenLoop("move.out_of_water");
    public static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("misc.grab");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // --- Fields ---
    private int hurtTimeCoolDown = 0;
    private int grabDamageTicks = 0;

    private int curioFeedCooldown = 0;
    private int curioSwimWithPlayerCooldown = 0;


    public Anomalocaris(EntityType<? extends AbstractBaseFish> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHunger(getMaxHunger());
    }

    // --- Goals & Attributes ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new AnomalocarisLayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AnomalocarisSwimWithPlayerGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AnomalocarisGiveItemGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new AnomalocarisGrabAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new AnomalocarisBreedGoal(this, 1.2D));
        this.goalSelector.addGoal(5, new AnomalocarisSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isSuitablePrey));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(AttributeInit.MAX_HUNGER.get());
    }

    // --- Data Sync ---
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HUNGER, 0f);
        this.entityData.define(DATA_HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_CURIO_PLAYER, Optional.empty());
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(DATA_GRABBING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Hunger", this.getHunger());
        tag.put("HeldItem", this.getHeldItem().save(new CompoundTag()));
        this.entityData.get(DATA_CURIO_PLAYER).ifPresent(uuid -> tag.putUUID("CurioPlayer", uuid));
        tag.putInt("curioFeedCooldown", this.curioFeedCooldown);
        tag.putInt("curioSwimWithPlayerCooldown", this.curioSwimWithPlayerCooldown);
        tag.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHunger(tag.getFloat("Hunger"));
        this.setHeldItem(ItemStack.of(tag.getCompound("HeldItem")));
        if (tag.hasUUID("CurioPlayer")) {
            this.setCurioPlayer(tag.getUUID("CurioPlayer"));
        }
        this.curioFeedCooldown = tag.getInt("curioFeedCooldown");
        this.curioSwimWithPlayerCooldown = tag.getInt("curioSwimWithPlayerCooldown");
        this.setHasEgg(tag.getBoolean("HasEgg"));
    }

    @Override
    public boolean canBePickedUpWithBucket(Player player, InteractionHand hand) {
        return this.isBaby();
    }

    // --- Getters & Setters ---
    public float getHunger() {
        return this.entityData.get(DATA_HUNGER);
    }

    public void setHunger(float hunger) {
        this.entityData.set(DATA_HUNGER, Mth.clamp(hunger, 0.0F, getMaxHunger()));
    }

    public float getMaxHunger() {
        return (float) this.getAttributeValue(AttributeInit.MAX_HUNGER.get());
    }

    public ItemStack getHeldItem() {
        return this.entityData.get(DATA_HELD_ITEM);
    }

    public void setHeldItem(ItemStack stack) {
        this.entityData.set(DATA_HELD_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    public boolean hasHeldItem() {
        return !this.getHeldItem().isEmpty();
    }

    public Optional<UUID> getCurioPlayer() {
        return this.entityData.get(DATA_CURIO_PLAYER);
    }

    public void setCurioPlayer(@Nullable UUID pUuid) {
        if (!this.level().isClientSide && pUuid != null){
            this.curioSwimWithPlayerCooldown = this.curioFeedCooldown > 0 ? 7200 : 4800;
            //this.curioSwimWithPlayerCooldown = this.curioFeedCooldown > 0 ? 200 : 100;
        }
        this.entityData.set(DATA_CURIO_PLAYER, Optional.ofNullable(pUuid));
    }

    public boolean hasEgg() {
        return this.entityData.get(DATA_HAS_EGG);
    }

    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(DATA_HAS_EGG, hasEgg);
    }

    public boolean isGrabbing() {
        return this.entityData.get(DATA_GRABBING);
    }

    public void setGrabbing(boolean grabbing) {
        this.entityData.set(DATA_GRABBING, grabbing);
    }

    public boolean isGrabbedState() {
        return this.hasHeldItem() || this.hasGrabbedPrey();
    }

    public LivingEntity getGrabbedPrey() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof LivingEntity prey) {
            if (!prey.isRemoved() && prey.isAlive()) {
                return prey;
            }
        }
        return null;
    }

    public boolean hasGrabbedPrey() {
        return getGrabbedPrey() != null;
    }

    // --- Logic ---
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.curioFeedCooldown > 0){
                this.curioFeedCooldown--;
            }
            if (this.curioSwimWithPlayerCooldown > 0){
                this.curioSwimWithPlayerCooldown--;
            }

            float hunger = this.getHunger() - HUNGER_DECREASE_PER_TICK;
            this.setHunger(Math.max(hunger, 0.0F));

            if (this.hurtTimeCoolDown > 0) {
                this.hurtTimeCoolDown--;
            }

            if (this.hasGrabbedPrey()) {
                LivingEntity prey = this.getGrabbedPrey();
                if (prey != null) {
                    if (++this.grabDamageTicks % 20 == 0) {
                        prey.hurt(this.damageSources().mobAttack(this), 0.5F);
                    }
                    if (prey.getHealth() <= 0 || prey.isDeadOrDying()) {
                        this.eatPrey(prey);
                    }
                }
            } else {
                this.grabDamageTicks = 0;
            }

            if (this.isGrabbing() && !this.hasGrabbedPrey() && !this.hasHeldItem()) {
                this.setGrabbing(false);
            }
        }
    }

    private void eatPrey(LivingEntity prey) {
        prey.stopRiding();
        this.setHunger(this.getHunger() + HUNGER_GAIN_PER_PREY);
        this.heal(5.0F);
        this.setGrabbing(false);
        this.grabDamageTicks = 0;
    }

    public void grabPrey(LivingEntity prey) {
        if (!this.hasGrabbedPrey() && !this.hasHeldItem()) {
            prey.startRiding(this, true);
            this.setGrabbing(true);
        }
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        var controllingPassenger = super.getControllingPassenger();
        //ensure prey can never steer this entity and deactivate goals
        if (controllingPassenger != null && getGrabbedPrey() != null && getGrabbedPrey().getId() == controllingPassenger.getId())
            return null;
        return controllingPassenger;
    }

    private void dropHeldItemToPlayer(Player player) {
        if (!player.getInventory().add(this.getHeldItem())) {
            this.spawnAtLocation(this.getHeldItem(), 0.5F);
        }
        this.setCurioPlayer(null);
        this.setHeldItem(ItemStack.EMPTY);
        this.setGrabbing(false);
    }

    protected void dropHeldItem(){
        this.spawnAtLocation(this.getHeldItem(), 0.5F);
        this.setHeldItem(ItemStack.EMPTY);
        this.setGrabbing(false);
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (pPassenger instanceof LivingEntity) {
            double yOffset = -0.4;
            double forwardOffset = 0.5;
            double pitch = Math.toRadians(this.getXRot());
            double yaw = Math.toRadians(this.yBodyRot);
            double yRotated = yOffset * Math.cos(pitch) - forwardOffset * Math.sin(pitch);
            double zRotated = yOffset * Math.sin(pitch) + forwardOffset * Math.cos(pitch);
            double dx = -zRotated * Math.sin(yaw);
            double dz = zRotated * Math.cos(yaw);
            pCallback.accept(pPassenger, this.getX() + dx, this.getY() + yRotated, this.getZ() + dz);
        } else {
            super.positionRider(pPassenger, pCallback);
        }
    }

    // --- Interaction & Combat ---

    @Override
    public boolean hurt(DamageSource pSource, float amount) {
        boolean hurt = super.hurt(pSource, amount);
        if (hurt) {
            LivingEntity prey = this.getGrabbedPrey();
            if (prey != null) {
                prey.stopRiding();
            }
            if (this.hasHeldItem()) {
                this.dropHeldItem();
            }
            this.setGrabbing(false);
            this.hurtTimeCoolDown = 100;
        }
        return hurt;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }
        ItemStack stack = pPlayer.getItemInHand(pHand);
        boolean breedPredicate = this.isFood(stack) && this.canFallInLove() && this.getAge() == 0;

        if (!breedPredicate && !this.hasHeldItem() && !this.hasGrabbedPrey() && !stack.isEmpty() && !isBaby() && canGiveItemToPlayer(pPlayer)) {
            ItemStack given = stack.split(1);
            this.setHeldItem(given);
            this.setGrabbing(true);
            this.setCurioPlayer(pPlayer.getUUID());
            this.curioFeedCooldown = 24000;
            return InteractionResult.SUCCESS;
        }

        if (!breedPredicate && this.hasHeldItem() && canGiveItemToPlayer(pPlayer)) {
            this.dropHeldItemToPlayer(pPlayer);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    protected boolean canGiveItemToPlayer(LivingEntity player){
        //currently a distance of 3 blocks is accepted
        return this.distanceToSqr(player) < 9.0;
    }

    private boolean isSuitablePrey(LivingEntity target) {
        if (target.getType() == this.getType()) return false;
        return target.getBbHeight() <= PREY_HEIGHT_MAX && target.isAlive() && !(target instanceof Player) && target.isInWater();
    }

    // --- Breeding ---
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(LITTags.Items.ANOMALOCARIS_BREEDABLE_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.ANOMALOCARIS.get().create(pLevel);
    }

    // --- Sounds ---
    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    // --- Animation ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers
                .add(new AnimationController<>(this, "move", 5, this::movePredicate))
                .add(new AnimationController<>(this, "grab", 25, this::grabPredicate));
    }

    protected <E extends Anomalocaris> PlayState movePredicate(final AnimationState<E> event) {
        if (!this.isInWater()) {
            event.getController().setAnimation(OUT_OF_WATER);
        } else if (event.isMoving()) {
            event.getController().setAnimation(SWIM);
        } else {
            event.getController().setAnimation(SWIM);
        }
        return PlayState.CONTINUE;
    }

    private <E extends Anomalocaris> PlayState grabPredicate(AnimationState<E> event) {
        if (this.hasHeldItem() || this.hasGrabbedPrey()) {
            event.getController().setAnimation(GRAB);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    public static boolean checkAnomalocarisSpawnRules(EntityType<?> pType, LevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, RandomSource pRandom) {
        return Config.naturalSpawns && checkSurfaceWaterAnimalSpawnRules(pType, pLevel, pReason, pPos, pRandom);
    }

    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<?> pWaterAnimal, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        int i = pLevel.getSeaLevel();
        int j = i - 13;
        return pPos.getY() >= j && pPos.getY() <= i && pLevel.getFluidState(pPos.below()).is(FluidTags.WATER) && pLevel.getBlockState(pPos.above()).is(Blocks.WATER);
    }

    // --- Inner Classes (Goals) ---

    static class AnomalocarisGrabAttackGoal extends MeleeAttackGoal {
        private final Anomalocaris anomalocaris;
        private LivingEntity target;

        public AnomalocarisGrabAttackGoal(Anomalocaris anomalocaris, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
            super(anomalocaris, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
            this.anomalocaris = anomalocaris;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.target = this.anomalocaris.getTarget();
            return !this.anomalocaris.isBaby() &&
                    this.target != null && this.target.isAlive()
                    && this.anomalocaris.getHunger() <= HUNGER_THRESHOLD
                    && !this.anomalocaris.hasGrabbedPrey()
                    && !this.anomalocaris.hasHeldItem();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.anomalocaris.isBaby() &&
                    this.target != null && this.target.isAlive()
                    && !this.anomalocaris.hasGrabbedPrey()
                    && !this.anomalocaris.hasHeldItem();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
            double d0 = this.getAttackReachSqr(pEnemy);
            if (pDistToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.anomalocaris.grabPrey(this.target);
            }
        }
    }

    static class AnomalocarisSwimmingGoal extends Goal {

        public static final int DEFAULT_INTERVAL = 120;
        protected final Anomalocaris mob;
        protected double wantedX;
        protected double wantedY;
        protected double wantedZ;
        protected final double speedModifier;
        protected int interval;
        protected boolean forceTrigger;
        private final boolean checkNoActionTime;

        public AnomalocarisSwimmingGoal(Anomalocaris pMob, double pSpeedModifier) {
            this(pMob, pSpeedModifier, DEFAULT_INTERVAL);
        }

        public AnomalocarisSwimmingGoal(Anomalocaris pMob, double pSpeedModifier, int pInterval) {
            this(pMob, pSpeedModifier, pInterval, true);
        }

        public AnomalocarisSwimmingGoal(Anomalocaris pMob, double pSpeedModifier, int pInterval, boolean pCheckNoActionTime) {
            this.mob = pMob;
            this.speedModifier = pSpeedModifier;
            this.interval = pInterval;
            this.checkNoActionTime = pCheckNoActionTime;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }

        }

        @javax.annotation.Nullable
        protected Vec3 getPosition() {
            return BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            this.mob.getNavigation().stop();
            super.stop();
        }

        /**
         * Makes task to bypass chance
         */
        public void trigger() {
            this.forceTrigger = true;
        }

        /**
         * Changes task random possibility for execution
         */
        public void setInterval(int pNewchance) {
            this.interval = pNewchance;
        }
    }

    static class AnomalocarisBreedGoal extends BreedGoal {

        protected Anomalocaris anomalocaris;

        public AnomalocarisBreedGoal(Anomalocaris pAnimal, double pSpeedModifier) {
            super(pAnimal, pSpeedModifier);
            this.anomalocaris = pAnimal;
        }

        public AnomalocarisBreedGoal(Anomalocaris pAnimal, double pSpeedModifier, Class<? extends Animal> pPartnerClass) {
            super(pAnimal, pSpeedModifier, pPartnerClass);
            this.anomalocaris = pAnimal;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !anomalocaris.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayer serverplayer = this.animal.getLoveCause();
            if (serverplayer == null && this.partner.getLoveCause() != null) {
                serverplayer = this.partner.getLoveCause();
            }
            if (serverplayer != null) {
                serverplayer.awardStat(Stats.ANIMALS_BRED);
                assert this.partner != null;
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, (AgeableMob) null);
            }

            int cooldown = Config.anomalocarisBreedCooldown + level.random.nextInt(1500) - level.random.nextInt(3000);

            this.anomalocaris.setHasEgg(true);

            this.animal.setAge(cooldown);
            this.partner.setAge(cooldown);
            this.animal.resetLove();
            this.partner.resetLove();
            RandomSource randomsource = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(),
                        randomsource.nextInt(7) + 1));
            }
        }
    }
    
    

    static class AnomalocarisLayEggGoal extends MoveToBlockGoal {
        private final Anomalocaris anomalocaris;

        public AnomalocarisLayEggGoal(Anomalocaris anomalocaris, double speedModifier) {
            super(anomalocaris, speedModifier, 16);
            this.anomalocaris = anomalocaris;
        }

        @Override
        public boolean canUse() {
            return this.anomalocaris.hasEgg() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.anomalocaris.hasEgg();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                this.anomalocaris.level().setBlock(this.blockPos.above(), BlockInit.ANOMALOCARIS_ROE.get().defaultBlockState(), 3);
                this.anomalocaris.setHasEgg(false);
                this.anomalocaris.setInLoveTime(600);
            }
        }

        @Override
        public double acceptedDistance() {
            return 2d;
        }

        @Override
        protected boolean isValidTarget(net.minecraft.world.level.LevelReader pLevel, BlockPos pPos) {
            return pLevel.getBlockState(pPos).is(Blocks.WATER) && pLevel.getBlockState(pPos.above()).isAir();
        }
    }

    static class AnomalocarisSwimWithPlayerGoal extends Goal {
        private final Anomalocaris anomalocaris;
        private final double speedModifier;
        @Nullable
        private Player player;

        AnomalocarisSwimWithPlayerGoal(Anomalocaris pAnomalocaris, double pSpeedModifier) {
            this.anomalocaris = pAnomalocaris;
            this.speedModifier = pSpeedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            var playerUuidOptional = this.anomalocaris.getCurioPlayer();
            if (playerUuidOptional.isEmpty()) {
                return false;
            }
            this.player = this.anomalocaris.level().getPlayerByUUID(playerUuidOptional.get());
            return this.player != null && this.player.isSwimming() && this.anomalocaris.distanceToSqr(this.player) < 256.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.player != null && this.player.isSwimming() && this.anomalocaris.distanceToSqr(this.player) < 256.0D && this.anomalocaris.getCurioPlayer().isPresent()
                    && this.anomalocaris.curioSwimWithPlayerCooldown >= 0;
        }

        @Override
        public void stop() {
            this.player = null;
            this.anomalocaris.getNavigation().stop();
        }

        @Override
        public void tick() {
            assert this.player != null;
            this.anomalocaris.getLookControl().setLookAt(this.player, (float) (this.anomalocaris.getMaxHeadYRot() + 20), (float) this.anomalocaris.getMaxHeadXRot());
            if (this.anomalocaris.distanceToSqr(this.player) < 6.25D) {
                this.anomalocaris.getNavigation().stop();
            } else {
                this.anomalocaris.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
    }

    static class AnomalocarisGiveItemGoal extends Goal {
        private final Anomalocaris anomalocaris;
        private final double speedModifier;
        @Nullable
        private Player targetPlayer;
        private int failTicks;

        public AnomalocarisGiveItemGoal(Anomalocaris pAnomalocaris, double pSpeedModifier) {
            this.anomalocaris = pAnomalocaris;
            this.speedModifier = pSpeedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.anomalocaris.hasHeldItem() || this.anomalocaris.curioSwimWithPlayerCooldown > 0) {
                return false;
            }
            Optional<UUID> playerUuid = this.anomalocaris.getCurioPlayer();
            if (playerUuid.isPresent()) {
                this.targetPlayer = this.anomalocaris.level().getPlayerByUUID(playerUuid.get());
                return this.targetPlayer != null && this.targetPlayer.isAlive() && !this.targetPlayer.isSpectator();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.failTicks = 0;
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.anomalocaris.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.targetPlayer != null) {
                this.anomalocaris.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float) this.anomalocaris.getMaxHeadXRot());

                if (this.anomalocaris.canGiveItemToPlayer(this.targetPlayer)) {
                    this.anomalocaris.dropHeldItemToPlayer(this.targetPlayer);
                } else {
                    if (!this.anomalocaris.getNavigation().moveTo(this.targetPlayer, this.speedModifier)) {
                        this.failTicks++;
                    } else if (this.anomalocaris.getNavigation().isStuck()) {
                        this.failTicks++;
                    } else if (this.failTicks > 0) {
                        this.failTicks--;
                    }
                }

                if (this.failTicks >= 100) {
                    this.anomalocaris.dropHeldItem();
                    this.anomalocaris.setCurioPlayer(null);
                }
            }
        }
    }
}
