package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.enums.HylonomusVariant;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Hylonomus extends Animal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_SLEEPING =
            SynchedEntityData.defineId(Hylonomus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(Hylonomus.class, EntityDataSerializers.INT);

    //ANIMATION
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int panicTicks = 0;


    public Hylonomus(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.5D, 2.0D, (entity) -> {
            return !(entity instanceof Hylonomus);
        }) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.6D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(LITTags.Items.HYLONOMUS_BREEDABLE_FOOD), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SLEEPING, false);
        this.entityData.define(DATA_VARIANT, HylonomusVariant.STRIPPED.getId());
    }

    public boolean isSleeping() {
        return this.entityData.get(DATA_SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(DATA_SLEEPING, sleeping);
    }

    public HylonomusVariant getVariant() {
        return HylonomusVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(HylonomusVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        /*Hylonomus baby = EntityInit.HYLONOMUS.get().create(pLevel);
        if (baby != null) {
            baby.setVariant(this.getVariant());
        }
        return baby;*/
        return null;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        ItemStack eggStack = new ItemStack(ItemInit.HYLONOMUS_EGG.get());
        eggStack.getOrCreateTag().putInt("Variant", this.getVariant().getId());

        this.spawnAtLocation(eggStack);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

        this.setAge(6000);
        pMate.setAge(6000);
        this.resetLove();
        pMate.resetLove();
        pLevel.broadcastEntityEvent(this, (byte)18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, this.getX(), this.getY(), this.getZ(), this.random.nextInt(7) + 1));
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(LITTags.Items.HYLONOMUS_BREEDABLE_FOOD);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Variant")) {
            this.setVariant(HylonomusVariant.byId(pCompound.getInt("Variant")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant().getId());
    }

    //MOVEMENT
    @Override
    public void customServerAiStep() {
        if (this.isSleeping()) {
            this.getNavigation().stop();
            this.setSprinting(false);
            return;
        }
        super.customServerAiStep();
        this.setSprinting(this.getMoveControl().hasWanted() &&
                this.getMoveControl().getSpeedModifier() >= 1.5D);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean hurt = super.hurt(pSource, pAmount);
        if (hurt) {
            int ticks = 100 + this.random.nextInt(100);
            this.panicTicks = ticks;
            List<? extends Hylonomus> hylonomuses = this.level().getEntitiesOfClass(Hylonomus.class,
                    this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            for (Hylonomus hylonomus : hylonomuses) {
                hylonomus.panicTicks = ticks;
            }
        }
        return hurt;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (panicTicks >= 0) {
                panicTicks--;
            }
            if (panicTicks == 0 && this.getLastHurtByMob() != null) {
                this.setLastHurtByMob(null);
            }

            boolean shouldSleep = this.level().isNight() && !this.isInPanic();

            if (shouldSleep != this.isSleeping()) {
                this.setSleeping(shouldSleep);
                System.out.println("Hylonomus " + this.getId() + " sleeping = " + shouldSleep);
            }
        }
    }


    private boolean isInPanic() {
        return panicTicks > 0;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        Holder<Biome> biome = pLevel.getBiome(this.blockPosition());

        if (biome.is(Biomes.BAMBOO_JUNGLE) || biome.is(Biomes.CHERRY_GROVE) || biome.is(Biomes.DARK_FOREST)) {
            this.setVariant(HylonomusVariant.LEAF);
        } else if (biome.is(BiomeTags.IS_TAIGA)) {
            this.setVariant(HylonomusVariant.ROCK);
        } else if (biome.is(BiomeTags.IS_SAVANNA)) {
            this.setVariant(HylonomusVariant.RUSTY);
        } else if (biome.is(BiomeTags.IS_JUNGLE)) {
            this.setVariant(HylonomusVariant.SPOTTED);
        } else if (biome.is(BiomeTags.IS_BEACH)) {
            this.setVariant(HylonomusVariant.STELAR);
        } else {
            this.setVariant(HylonomusVariant.STRIPPED);
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static boolean checkHylonomusSpawnRules(EntityType<Hylonomus> pEntityType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (pSpawnType == MobSpawnType.SPAWN_EGG) {
            return true;
        }
        boolean isLightEnough = pLevel.getRawBrightness(pPos, 0) > 8;

        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        boolean isValidBlock = blockBelow.is(BlockTags.ANIMALS_SPAWNABLE_ON)
                || blockBelow.is(BlockTags.SAND)
                || blockBelow.is(BlockTags.DIRT);

        return isLightEnough && isValidBlock;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    //ANIMATION
    private <T extends Hylonomus> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
                event.getController().setAnimationSpeed(2.3D);
            } else {
                event.getController().setAnimation(WALK);
                event.getController().setAnimationSpeed(1.5D);
            }
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(IDLE);
        event.getController().setAnimationSpeed(1.0D);
        return PlayState.CONTINUE;
    }
}
