package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DodoModel extends DefaultedEntityGeoModel<Dodo> {

    public DodoModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "dodo"));
    }

    @Override
    public void setCustomAnimations(Dodo animatable, long instanceId, AnimationState<Dodo> animationState) {
        if (animationState == null) return;

        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck");

        if (neck != null) {
            EntityModelData extraDataOfType = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (animatable.isBaby()) {
                neck.setScaleX(1.2F);
                neck.setScaleY(1.2F);
                neck.setScaleZ(1.2F);
            } else {
                neck.setScaleX(1.0F);
                neck.setScaleY(1.0F);
                neck.setScaleZ(1.0F);
            }

            if (!animatable.isPecking()) {
                neck.setRotX(extraDataOfType.headPitch() * Mth.DEG_TO_RAD);
                neck.setRotY(extraDataOfType.netHeadYaw() * Mth.DEG_TO_RAD);
            }

        }
    }

    @Override
    public ResourceLocation getTextureResource(Dodo animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/dodo_baby.png") : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Dodo animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/dodo_baby.geo.json") : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Dodo animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/dodo_baby.animation.json") : super.getAnimationResource(animatable);
    }
}
