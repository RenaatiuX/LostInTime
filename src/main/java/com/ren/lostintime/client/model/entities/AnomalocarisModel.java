package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Anomalocaris;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AnomalocarisModel extends DefaultedEntityGeoModel<Anomalocaris> {

    private static final String BABY_TEXTURE = "textures/entity/anomalocaris_baby.png";
    private static final String BABY_ANIM = "animations/entity/anomalocaris_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/anomalocaris_baby.geo.json";

    public AnomalocarisModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "anomalocaris"));
    }

    @Override
    public void setCustomAnimations(Anomalocaris animatable, long instanceId, AnimationState<Anomalocaris> animationState) {
        CoreGeoBone body = getAnimationProcessor().getBone("body");
        if (animatable.isInWater()) {
            if (body != null) {
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
                body.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            }
        }
    }

    @Override
    public ResourceLocation getTextureResource(Anomalocaris animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE) : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Anomalocaris animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO) : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Anomalocaris animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM) : super.getAnimationResource(animatable);
    }
}
