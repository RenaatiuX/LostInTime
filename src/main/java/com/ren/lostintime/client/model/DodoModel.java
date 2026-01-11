package com.ren.lostintime.client.model;

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
        super(new ResourceLocation(LostInTime.MODID, "dodo"));
    }

    @Override
    public void setCustomAnimations(Dodo animatable, long instanceId, AnimationState<Dodo> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if (animationState == null) return;

        EntityModelData extraDataOfType = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck");

        if (neck != null) {
            if (animatable.isBaby()) {
                neck.setScaleX(1.2F);
                neck.setScaleY(1.2F);
                neck.setScaleZ(1.2F);
            } else {
                neck.setScaleX(1.0F);
                neck.setScaleY(1.0F);
                neck.setScaleZ(1.0F);
            }

            /*if (!animatable.getIsPecking()) {
                neck.setRotX(extraDataOfType.headPitch() * Mth.DEG_TO_RAD);
                neck.setRotY(extraDataOfType.netHeadYaw() * Mth.DEG_TO_RAD);
            }*/

        }
    }
}
