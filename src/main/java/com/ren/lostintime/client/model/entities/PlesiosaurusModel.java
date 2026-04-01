package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Plesiosaurus;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PlesiosaurusModel extends DefaultedEntityGeoModel<Plesiosaurus> {

    private static final ResourceLocation ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/plesiosaurus.geo.json");
    private static final ResourceLocation YOUNG_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/plesiosaurus_young.geo.json");
    private static final ResourceLocation BABY_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/plesiosaurus_baby.geo.json");

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/plesiosaurus/plesiosaurus.png");
    private static final ResourceLocation YOUNG_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/plesiosaurus/plesiosaurus_young.png");
    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/plesiosaurus/plesiosaurus_baby.png");

    private static final ResourceLocation ADULT_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/plesiosaurus.animation.json");
    private static final ResourceLocation YOUNG_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/plesiosaurus_young.animation.json");
    private static final ResourceLocation BABY_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/plesiosaurus_baby.animation.json");


    public PlesiosaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "plesiosaurus"));
    }

    @Override
    public ResourceLocation getTextureResource(Plesiosaurus animatable) {
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
    public ResourceLocation getModelResource(Plesiosaurus animatable) {
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
    public ResourceLocation getAnimationResource(Plesiosaurus animatable) {
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
    public void setCustomAnimations(Plesiosaurus animatable, long instanceId, AnimationState<Plesiosaurus> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone neck = getAnimationProcessor().getBone("neck");
        CoreGeoBone neck2 = getAnimationProcessor().getBone("neck2");
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (neck != null && neck2 != null && head != null) {
            float pitch = entityData.headPitch() * Mth.DEG_TO_RAD;
            float yaw = entityData.netHeadYaw() * Mth.DEG_TO_RAD;

            neck.setRotY(yaw * 0.35F);
            neck2.setRotY(yaw * 0.25F);
            head.setRotY(yaw * 0.40F);

            neck.setRotX(pitch * 0.60F);
            neck2.setRotX(pitch * -0.20F);
            head.setRotX(pitch * 0.60F);
        }
    }
}
