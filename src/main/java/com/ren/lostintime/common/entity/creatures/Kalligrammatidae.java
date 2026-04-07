package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.ai.KalligrammatidaeAi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Kalligrammatidae extends AmbientCreature implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> IS_LANDED = SynchedEntityData.defineId(Kalligrammatidae.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> ATTACH_FACE = SynchedEntityData.defineId(Kalligrammatidae.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Kalligrammatidae.class, EntityDataSerializers.INT);

    protected static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public boolean wantsToLand = false;

    public Kalligrammatidae(EntityType<? extends AmbientCreature> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, false);
        this.setNoGravity(true);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, pLevel);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AmbientCreature.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setLanded(false);
        this.wantsToLand = false;
        this.setVariant(this.random.nextInt(3));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    // ==========================================
    // BRAIN
    // ==========================================
    @Override
    protected Brain.Provider<Kalligrammatidae> brainProvider() {
        return Brain.provider(KalligrammatidaeAi.MEMORY_TYPES, ImmutableList.of());
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Kalligrammatidae> brain = this.brainProvider().makeBrain(pDynamic);
        return KalligrammatidaeAi.makeBrain(brain);
    }

    @Override
    public Brain<Kalligrammatidae> getBrain() {
        return (Brain<Kalligrammatidae>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("kalligrammatidaeBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    // ==========================================
    // SYNCED DATA
    // ==========================================
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LANDED, false);
        this.entityData.define(ATTACH_FACE, (byte) Direction.UP.get3DDataValue());
        this.entityData.define(VARIANT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(pCompound.getInt("Variant"));
    }

    public boolean isLanded() {
        return this.entityData.get(IS_LANDED);
    }

    public void setLanded(boolean landed) {
        this.entityData.set(IS_LANDED, landed);
    }

    public Direction getAttachFace() {
        return Direction.from3DDataValue(this.entityData.get(ATTACH_FACE));
    }

    public void setAttachFace(Direction dir) {
        this.entityData.set(ATTACH_FACE, (byte) dir.get3DDataValue());
    }

    public int getVariant() {
        return this.entityData.get(VARIANT_ID);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT_ID, variant);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, @NotNull BlockState pState, @NotNull BlockPos pPos) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5,
                event -> {
                    if (this.isLanded()) {
                        return event.setAndContinue(IDLE);
                    }
                    return event.setAndContinue(FLY);
                }));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
