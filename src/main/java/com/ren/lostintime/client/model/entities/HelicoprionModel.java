package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HelicoprionModel extends DefaultedEntityGeoModel<Helicoprion> {

    private static final ResourceLocation ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/helicoprion.geo.json");
    private static final ResourceLocation YOUNG_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/helicoprion_young.geo.json");
    private static final ResourceLocation BABY_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/helicoprion_baby.geo.json");

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/helicoprion/helicoprion.png");
    private static final ResourceLocation YOUNG_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/helicoprion/helicoprion_young.png");
    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/helicoprion/helicoprion_baby.png");

    private static final ResourceLocation ADULT_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/helicoprion.animation.json");
    private static final ResourceLocation YOUNG_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/helicoprion_young.animation.json");
    private static final ResourceLocation BABY_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/helicoprion_baby.animation.json");

    public HelicoprionModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "helicoprion"));
    }

    @Override
    public ResourceLocation getModelResource(Helicoprion animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_MODEL;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_MODEL;
        } else {
            return ADULT_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(Helicoprion animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_TEXTURE;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_TEXTURE;
        } else {
            return ADULT_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(Helicoprion animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_ANIM;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_ANIM;
        } else {
            return ADULT_ANIM;
        }
    }

    @Override
    public void setCustomAnimations(Helicoprion animatable, long instanceId, AnimationState<Helicoprion> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone rootBone = this.getAnimationProcessor().getBone("body");

        if (rootBone != null) {

            float partialTick = animationState.getPartialTick();
            float smoothPitch = Mth.lerp(partialTick, animatable.prevBreachPitch, animatable.breachPitch);

            if (animatable.breachPitch != 0.0F || animatable.prevBreachPitch != 0.0F) {
                rootBone.setRotX(-(smoothPitch * ((float)Math.PI / 180F)));
            }
        }
    }
}
