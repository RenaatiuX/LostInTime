package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.EndocerasAi;
import com.ren.lostintime.common.entity.ai.HelicoprionAi;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Helicoprion extends LITWaterAnimal implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("flop");
    private static final RawAnimation ATTACK_RAM = RawAnimation.begin().thenPlay("attack_ram");
    private static final RawAnimation ATTACK_SAW = RawAnimation.begin().thenPlay("attack_saw");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Helicoprion(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected @NotNull Brain.Provider<Helicoprion> brainProvider() {
        return Brain.provider(HelicoprionAi.MEMORY_TYPES, HelicoprionAi.SENSOR_TYPES);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Helicoprion> brain = this.brainProvider().makeBrain(pDynamic);
        HelicoprionAi.makeBrain(brain);
        return brain;
    }

    @SuppressWarnings("unchecked")
    public Brain<Helicoprion> getBrain() {
        return (Brain<Helicoprion>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("helicoprionBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        HelicoprionAi.updateActivity(this);

        super.customServerAiStep();
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return null;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        ServerPlayer serverplayer = this.getLoveCause();
        if (serverplayer == null && pMate.getLoveCause() != null) {
            serverplayer = pMate.getLoveCause();
        }

        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this, pMate, null);
        }

        this.setAge(6000);
        pMate.setAge(6000);
        this.resetLove();
        pMate.resetLove();

        //this.spawnAtLocation(ItemInit.HELICOPRION_EGG_CASE.get(), 1);

        pLevel.broadcastEntityEvent(this, (byte)18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.COD) || pStack.is(Items.SALMON);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean canSleep() {
        return false;
    }
}
