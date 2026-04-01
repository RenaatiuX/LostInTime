package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Deinonychus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DeinonychusModel extends DefaultedEntityGeoModel<Deinonychus> {

    private static final ResourceLocation ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/deinonychus.geo.json");
    private static final ResourceLocation BABY_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/deinonychus_baby.geo.json");

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/deinonychus/deinonychus.png");
    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/deinonychus/deinonychus_baby.png");

    private static final ResourceLocation ADULT_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/deinonychus.animation.json");
    private static final ResourceLocation BABY_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/deinonychus_baby.animation.json");

    public DeinonychusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "deinonychus"));
    }

    @Override
    public ResourceLocation getModelResource(Deinonychus animatable) {
        return animatable.isBaby() ? BABY_MODEL : ADULT_MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(Deinonychus animatable) {
        return animatable.isBaby() ? BABY_ANIM : ADULT_ANIM;
    }

    @Override
    public ResourceLocation getTextureResource(Deinonychus animatable) {
        return animatable.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;
    }

    @Override
    public void setCustomAnimations(Deinonychus animatable, long instanceId, AnimationState<Deinonychus> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        //if he is sleeping, he does not move his head
        if (animatable.isSleeping()) return;

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        CoreGeoBone neck = getAnimationProcessor().getBone("neck");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            float pitch = entityData.headPitch() * Mth.DEG_TO_RAD;
            float yaw = entityData.netHeadYaw() * Mth.DEG_TO_RAD;

            if (!animatable.isStalking() || !animatable.isEating()) {
                if (neck != null) {
                    neck.setRotX(pitch * 0.4F);
                    neck.setRotY(yaw * 0.4F);
                }
                head.setRotX(pitch * 0.6F);
                head.setRotY(yaw * 0.6F);
            }
        }
    }
}
