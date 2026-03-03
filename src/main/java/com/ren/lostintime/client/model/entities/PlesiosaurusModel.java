package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Plesiosaurus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PlesiosaurusModel extends DefaultedEntityGeoModel<Plesiosaurus> {

    public PlesiosaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "plesiosaurus"), true);
    }

    @Override
    public ResourceLocation getTextureResource(Plesiosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/plesiosaurus_baby.png") : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Plesiosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/plesiosaurus_baby.geo.json") : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Plesiosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/plesiosaurus_baby.animation.json") : super.getAnimationResource(animatable);
    }
}
