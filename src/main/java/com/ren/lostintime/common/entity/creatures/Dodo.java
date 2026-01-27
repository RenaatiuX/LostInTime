package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.goal.EggBreedGoal;
import com.ren.lostintime.common.entity.goal.LayEggGoal;
import com.ren.lostintime.common.entity.util.IEggLayer;
import com.ren.lostintime.common.entity.util.ISleepingEntity;
import com.ren.lostintime.common.init.*;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class Dodo extends LITAnimal implements GeoEntity, IEggLayer, ISleepingEntity {

    private static final EntityDataAccessor<Boolean> PECKING = SynchedEntityData.defineId(Dodo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Dodo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Dodo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(Dodo.class, EntityDataSerializers.BOOLEAN);

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    protected static final RawAnimation FLAP = RawAnimation.begin().thenLoop("misc.flap");
    protected static final RawAnimation PECK = RawAnimation.begin().thenLoop("misc.peck");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    public static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("misc.sleep");
    public static final RawAnimation NO = RawAnimation.begin().thenLoop("misc.no");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //egg
    private int eggCounter;

    //flap
    public float flapping = 1.0F;

    //zombie
    public boolean isChickenJockey;

    //sleep particles
    private int sleepParticleCooldown = 0;

    //Peck logic
    private BlockPos peckTarget;
    private int peckCooldown = 0;
    private float lootMultiplier = 1;
    private int goldenFoodBonus = 0;
    protected int goldenCooldown = 0;

    public Dodo(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(1, new DodoPeckGoal(this));
        this.goalSelector.addGoal(2, new DodoEatFruitGoal(this));
        this.goalSelector.addGoal(4, new DodoWalkToPeckTarget(this, 1.0D));
        this.goalSelector.addGoal(5, new EggBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new LayEggGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.0D, Ingredient.of(LITTags.Items.DODO_FOOD), false));
        this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new AvoidEntityGoal<>(this, AbstractIllager.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return this.isBaby() ? pDimensions.height * 0.85F : pDimensions.height * 0.92F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(LITTags.Items.DODO_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.DODO.get().create(pLevel);
    }

    public boolean canEatPeckFood(ItemStack stack){
        if (Config.goldenFoodMultipliers.containsKey(stack.getItem()) && goldenCooldown <= 0)
            return true;
        return stack.is(LITTags.Items.FRUITS);
    }


    public void feedPeckItem(ItemStack stack){
        if (!canEatPeckFood(stack))
            return;
        if (Config.goldenFoodMultipliers.containsKey(stack.getItem())){
            this.goldenFoodBonus = 10;
            this.lootMultiplier = Config.goldenFoodMultipliers.get(stack.getItem());
            goldenCooldown = 24000;//24000 is the time one day and night in minecraft takes
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.eggCounter > 0){
            this.eggCounter--;
        }
        if (peckCooldown > 0){
            this.peckCooldown--;
        }
        if (goldenCooldown > 0){
            goldenCooldown--;
        }
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        if (this.level().isClientSide() && this.isSleeping()) {
            spawnSleepingParticles();
        }
    }

    private void spawnSleepingParticles() {
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
    protected void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.2D);
        } else {
            this.setSprinting(false);
            this.flapping = 0.9F;
        }
        super.customServerAiStep();
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isSleeping() && super.canBeLeashed(player);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PECKING, false);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(LAYING_EGG, false);
        this.entityData.define(SLEEPING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isPecking", this.isPecking());
        pCompound.putBoolean("HasEgg", this.hasEgg());
        pCompound.putInt("eggCounter", this.eggCounter);
        pCompound.putInt("goldenCooldown", this.goldenCooldown);
        pCompound.putInt("peckCooldown", this.peckCooldown);
        pCompound.putInt("goldenFoodBonus", this.goldenFoodBonus);
        pCompound.putFloat("lootMultiplier", this.lootMultiplier);
        pCompound.putBoolean("IsChickenJockey", this.isChickenJockey);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setPecking(pCompound.getBoolean("isPecking"));
        this.entityData.set(HAS_EGG, pCompound.getBoolean("HasEgg"));
        this.eggCounter = pCompound.getInt("eggCounter");
        this.goldenCooldown = pCompound.getInt("goldenCooldown");
        this.peckCooldown = pCompound.getInt("peckCooldown");
        this.goldenFoodBonus = pCompound.getInt("goldenFoodBonus");
        this.lootMultiplier = pCompound.getFloat("lootMultiplier");
        this.isChickenJockey = pCompound.getBoolean("IsChickenJockey");
    }

    //Pecking
    public boolean isPecking() {
        return this.entityData.get(PECKING);
    }

    public void setPecking(boolean pecking) {
        this.entityData.set(PECKING, pecking);
    }

    //babe zombie
    public boolean isChickenJockey() {
        return this.isChickenJockey;
    }

    public void setChickenJockey(boolean pIsChickenJockey) {
        this.isChickenJockey = pIsChickenJockey;
    }

    //Sleep
    @Override
    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    @Override
    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    @Override
    public boolean canSleep() {
        return !this.isInLove();
    }

    @Nullable
    public BlockPos getPeckTarget() {
        return peckTarget;
    }

    public void setPeckTarget(@Nullable BlockPos peckTarget) {
        this.peckTarget = peckTarget;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.setSleeping(false);
        return super.hurt(pSource, pAmount);
    }

    //Animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "sleepController", 5, this::sleepPredicate));
        controllers.add(new AnimationController<>(this, "flapController", 2, this::flapPredicate));
        controllers.add(new AnimationController<>(this, "moveController", 5, this::movePredicate));
        controllers.add(new AnimationController<>(this, "peckController", 2, state -> {
            if (this.isPecking()) {
                return state.setAndContinue(PECK);
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }));
    }

    protected <E extends Dodo> PlayState movePredicate(final AnimationState<E> event) {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
            } else {
                event.getController().setAnimation(WALK);
            }
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(IDLE);
            event.getController().setAnimationSpeed(1.0D);
        }
        return PlayState.CONTINUE;
    }

    protected <E extends Dodo> PlayState flapPredicate(final AnimationState<E> event) {
        if (!isSleeping() && !this.onGround() && level().getBlockState(this.getOnPos()).isAir() && !this.isInWater()) {

            event.getController().setAnimation(FLAP);
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    protected <E extends Dodo> PlayState sleepPredicate(AnimationState<E> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return super.createNavigation(pLevel);
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return this.isChickenJockey();
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        float f = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f2 = 0.15F;
        float f3 = 0.0F;
        pCallback.accept(pPassenger, this.getX() + (double) (f2 * f), this.getY(0.6D) + pPassenger.getMyRidingOffset() + f3,
                this.getZ() - (double) (f2 * f1));
        if (pPassenger instanceof LivingEntity) {
            ((LivingEntity) pPassenger).yBodyRot = this.yBodyRot;
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        if (!this.level().isClientSide() && !this.isBaby() && this.random.nextFloat() < 0.05F) {
            spawnChickenJockey((ServerLevel) pLevel);
        }

        return data;
    }

    private void spawnChickenJockey(ServerLevel world) {
        Zombie babyZombie = EntityType.ZOMBIE.create(world);
        if (babyZombie != null) {
            babyZombie.setBaby(true);
            babyZombie.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            babyZombie.startRiding(this);
            setChickenJockey(true);
            world.addFreshEntity(babyZombie);
        }
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    @Override
    public void hatchEgg(int hatchTicks) {
        if (!hasEgg()){
            this.entityData.set(HAS_EGG, true);
            this.eggCounter = hatchTicks;
        }
    }

    @Override
    public void onEggLayed(BlockPos eggPos) {
        this.entityData.set(HAS_EGG, false);
        this.level().levelEvent(2001, eggPos, Block.getId(this.level().getBlockState(eggPos.below())));
    }

    @Override
    public boolean validEggPosition(BlockPos pos, BlockState state) {
        return state.is(BlockTags.DIRT);
    }

    @Override
    public boolean canLayEgg() {
        return this.eggCounter <= 0;
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        return !hasEgg() && super.canMate(pOtherAnimal);
    }


    @Override
    public BlockState getEgg() {
        return BlockInit.DODO_EGG.get().defaultBlockState();
    }

    static class DodoEatFruitGoal extends Goal {

        private final Dodo dodo;
        private ItemEntity targetItem;

        public DodoEatFruitGoal(Dodo dodo) {
            this.dodo = dodo;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (dodo.isSleeping() || dodo.isPecking() || dodo.hasEgg() || dodo.peckCooldown > 0) return false;

            List<ItemEntity> items = dodo.level().getEntitiesOfClass(
                    ItemEntity.class,
                    dodo.getBoundingBox().inflate(6.0D), s -> dodo.canEatPeckFood(s.getItem())
            );

            if (!items.isEmpty()) {
                targetItem = items.get(0);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            dodo.getNavigation().moveTo(targetItem, 1.0D);
        }

        @Override
        public void tick() {
            if (targetItem == null) return;

            if (dodo.distanceTo(targetItem) < 1.2F) {
                dodo.setPecking(true);
                dodo.feedPeckItem(targetItem.getItem());
                targetItem.getItem().shrink(1);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return targetItem != null && targetItem.isAlive();
        }

        @Override
        public void stop() {
            targetItem = null;
        }
    }

    static class DodoWalkToPeckTarget extends MoveToBlockGoal {

        private final Dodo dodo;

        public DodoWalkToPeckTarget(Dodo dodo, double speedModifier) {
            super(dodo, speedModifier, 1);
            this.dodo = dodo;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !dodo.isSleeping() && !dodo.hasEgg() && dodo.peckTarget != null && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !dodo.isSleeping() && !dodo.hasEgg() && dodo.peckTarget != null && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            return true;
        }

        @Override
        protected boolean findNearestBlock() {
            this.blockPos = dodo.peckTarget;
            return true;
        }
    }

    static class DodoPeckGoal extends Goal {

        private final Dodo dodo;
        private int peckTime;

        public DodoPeckGoal(Dodo dodo) {
            this.dodo = dodo;
            //actually setting the move and Jump flag so the dodo doesnt execute move and jump and look goals
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return dodo.peckCooldown <= 0 && dodo.isPecking();
        }

        @Override
        public void start() {
            peckTime = 40 + dodo.getRandom().nextInt(20);
            dodo.setPeckTarget(null);
        }

        @Override
        public void tick() {
            peckTime--;
            if (peckTime % 10 == 0) {
                dodo.playSound(SoundEvents.CHICKEN_STEP, 0.4F, 1.2F);
                dodo.level().gameEvent(GameEvent.BLOCK_PLACE, dodo.getOnPos(), GameEvent.Context.of(this.dodo, dodo.getBlockStateOn()));
            }
        }

        @Override
        public boolean canContinueToUse() {
            return peckTime > 0;
        }

        @Override
        public void stop() {
            dodo.setPecking(false);
            spawnDodoLoot();
        }

        protected void spawnDodoLoot(){
            if (dodo.level() instanceof ServerLevel serverLevel){
                if (dodo.goldenFoodBonus > 0){
                    dodo.goldenFoodBonus--;
                }else {
                    dodo.lootMultiplier = 1f;
                }
                LootTable loottable = serverLevel.getServer().getLootData().getLootTable(ModLootTables.DODO_PECK_LOOT);
                LootParams params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.BLOCK_STATE, dodo.getBlockStateOn()).withParameter(LootContextParams.ORIGIN, dodo.getOnPos().getCenter()).withParameter(LootContextParams.THIS_ENTITY, dodo).withParameter(ModLootParamSets.DODO_GOLDEN_FOOD_MULTIPLIER, dodo.lootMultiplier).create(ModLootParamSets.DODO_PECK);

                var dropItems = loottable.getRandomItems(params);
                var spawnPos = dodo.getOnPos().above();
                for(var item : dropItems){
                    ItemEntity itementity = new ItemEntity(serverLevel, (double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), item);
                    serverLevel.addFreshEntity(itementity);
                }
            }
        }
    }
}
