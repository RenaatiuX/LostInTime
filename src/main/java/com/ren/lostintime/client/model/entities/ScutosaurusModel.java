package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Scutosaurus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ScutosaurusModel extends DefaultedEntityGeoModel<Scutosaurus> {

    private static final String BABY_TEXTURE = "textures/entity/scutosaurus_baby.png";
    private static final String BABY_ANIM = "animations/entity/scutosaurus_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/scutosaurus_baby.geo.json";

    public ScutosaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "scutosaurus"), true);
    }

    @Override
    public ResourceLocation getTextureResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE) : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO) : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Scutosaurus animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM) : super.getAnimationResource(animatable);
    }
}
