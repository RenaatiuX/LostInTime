package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Scutosaurus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ScutosaurusModel extends DefaultedEntityGeoModel<Scutosaurus> {

    public ScutosaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "scutosaurus"), true);
    }

    @Override
    public ResourceLocation getTextureResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/scutosaurus_baby.png") : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/scutosaurus_baby.geo.json") : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/scutosaurus_baby.animation.json") : super.getAnimationResource(animatable);
    }
}
