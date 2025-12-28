package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.goal.CircleAroundGoal;
import com.ren.lostintime.common.entity.goal.FindDroppedFruitGoal;
import com.ren.lostintime.common.entity.goal.GoToPeckSpotGoal;
import com.ren.lostintime.common.entity.goal.PeckGoal;
import com.ren.lostintime.common.entity.util.IPeckerEntity;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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

import java.util.ArrayList;
import java.util.List;

public class Dodo extends Animal implements GeoEntity, IPeckerEntity {

    private static final EntityDataAccessor<Boolean> PECKING =
            SynchedEntityData.defineId(Dodo.class, EntityDataSerializers.BOOLEAN);

    public static final ResourceLocation PECK_LOOT =
            new ResourceLocation(LostInTime.MODID, "dodo/pecking");


    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    protected static final RawAnimation FLAP = RawAnimation.begin().thenLoop("misc.flap");
    protected static final RawAnimation PECK = RawAnimation.begin().thenPlay("misc.peck");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    public static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("misc.sleep");
    public static final RawAnimation NO = RawAnimation.begin().thenLoop("misc.no");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float flap;
    private float flapSpeed;
    private float oFlapSpeed;
    private float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private int eggTime;
    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;
    public BlockPos peckTarget;
    public int peckCooldown = 0;
    public PeckState peckState = PeckState.NONE;
    private int goldenBoostRolls = 0;
    private boolean goldenBoostUsedToday = false;
    private int currentGoldenMultiplier = 1;

    public Dodo(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.eggTime = this.random.nextInt(6000) + 6000;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new FindDroppedFruitGoal<>(this));
        this.goalSelector.addGoal(3, new GoToPeckSpotGoal<>(this));
        this.goalSelector.addGoal(4, new CircleAroundGoal<>(this));
        this.goalSelector.addGoal(5, new PeckGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        //TODO look u can create ingredients out of tags
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.0D, Ingredient.of(LITTags.Items.DODO_FOOD), false));
        this.goalSelector.addGoal(7, new AvoidEntityGoal<>(this, AbstractIllager.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.1D));
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(8, this.eatBlockGoal);
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
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

    //Ai
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
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
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.multiply(1.0, 0.6, 1.0));
        }

        this.flap += this.flapping * 2.0F;

        //Eat
        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }

        if (this.level() instanceof ServerLevel serverLevel && peckTarget != null) {

            BlockState state = serverLevel.getBlockState(peckTarget);
            if (!state.isAir()) {
                for (int i = 0; i < 3; i++) {
                    double px = peckTarget.getX() + 0.5 + (this.random.nextDouble() - 0.5) * 0.3;
                    double py = peckTarget.getY() + 0.1;
                    double pz = peckTarget.getZ() + 0.5 + (this.random.nextDouble() - 0.5) * 0.3;

                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, state),
                            px, py, pz,
                            3,
                            0, 0.05, 0,
                            1d
                    );
                }
            }
        }

        // Reset
        if (!this.level().isClientSide) {
            long time = this.level().getDayTime() % 24000L;
            if (time == 0) {
                goldenBoostUsedToday = false;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        // Peck
        if (peckCooldown > 0) {
            peckCooldown--;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PECKING, false);
    }

    public boolean isPecking() {
        return this.entityData.get(PECKING);
    }

    public void setPecking(boolean pecking) {
        this.entityData.set(PECKING, pecking);
    }

    public void startPecking() {
        setPecking(true);
        //TODO look this can be triggered like this from the server side
        triggerAnim("movePredicate", "peck");
    }

    @Override
    public void ate() {
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level().isClientSide)
            return InteractionResult.SUCCESS;

        //Golden food
        if (stack.is(LITTags.Items.GOLDEN_FOODS) && !goldenBoostUsedToday) {
            int mult = getGoldenFoodMultiplier(stack);
            this.currentGoldenMultiplier = mult;
            this.goldenBoostRolls = 10;
            this.goldenBoostUsedToday = true;
            stack.shrink(1);
            this.level().broadcastEntityEvent(this, (byte) 10);
            return InteractionResult.CONSUME;
        }

        //Normal food
        if (isFruit(stack) && peckCooldown <= 0 && peckState == PeckState.NONE) {
            stack.shrink(1);
            peckTarget = player.blockPosition().below();
            peckState = PeckState.MOVING;

            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }


    //Animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "stateController", 5, this::statePredicate)
                .receiveTriggeredAnimations()
                .triggerableAnim("peck", PECK));
        controllers.add(new AnimationController<>(this, "movePredicate", 5, this::movePredicate));
        controllers.add(new AnimationController<>(this, "flapController", 2, this::flapPredicate));
    }

    protected <E extends Dodo> PlayState statePredicate(AnimationState<E> event) {
        if (isNight()
                && getPeckState() == PeckState.NONE
                && this.onGround()
                && this.getDeltaMovement().lengthSqr() < 0.001) {
            event.getController().setAnimation(SLEEP);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    protected <E extends Dodo> PlayState movePredicate(final AnimationState<E> event) {
        if (getPeckState() != PeckState.NONE || isNight()) {
            return PlayState.STOP;
        }
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
            } else {
                event.getController().setAnimation(WALK);
            }
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    protected <E extends Dodo> PlayState flapPredicate(final AnimationState<E> event) {
        if (!this.onGround() && !this.isInWater()) {
            event.getController().setAnimation(FLAP);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isNight() {
        long time = this.level().getDayTime() % 24000L;
        return time >= 13000 && time <= 23000;
    }

    /**
     * only called on the server side
     */
    public void finishPecking() {
        generatePeckLoot();
        this.peckCooldown = 1000;
        setPecking(false);
    }

    private void generatePeckLoot() {
        ServerLevel server = (ServerLevel) this.level();
        LootTable table = server.getServer().getLootData().getLootTable(PECK_LOOT);

        LootParams params = new LootParams.Builder(server)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.GIFT);

        List<ItemStack> drops = new ArrayList<>();

        int rolls = 1;

        if (goldenBoostRolls > 0) {
            rolls = currentGoldenMultiplier;
            goldenBoostRolls--;
        }

        for (int i = 0; i < rolls; i++) {
            drops.addAll(table.getRandomItems(params));
        }

        for (ItemStack stack : drops) {
            this.spawnAtLocation(stack);
        }
    }

    public boolean isValidSoil(BlockState state) {
        return state.is(LITTags.Blocks.DODO_SOILS);
    }

    @Override
    public boolean isFruit(ItemStack stack) {
        return stack.is(LITTags.Items.FRUITS);
    }

    public int getGoldenFoodMultiplier(ItemStack stack) {
        if (!stack.is(LITTags.Items.GOLDEN_FOODS)) return 1;

        if (stack.is(Items.GOLDEN_CARROT)) return 2;
        if (stack.is(Items.GOLDEN_APPLE)) return 4;
        if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) return 8;

        return 2;
    }


    @Override
    public void setPeckTarget(BlockPos target) {
        this.peckTarget = target;
    }

    @Override
    public BlockPos getPeckTarget() {
        return this.peckTarget;
    }

    @Override
    public PeckState getPeckState() {
        return this.peckState;
    }

    @Override
    public void setPeckState(PeckState state) {
        this.peckState = state;
        if (state != PeckState.PECKING) {
            setPecking(false);
        }
    }

    @Override
    public boolean canPeck() {
        return peckCooldown <= 0;
    }
}
