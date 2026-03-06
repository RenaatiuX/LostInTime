package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Endoceras;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndocerasModel extends DefaultedEntityGeoModel<Endoceras> {

    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/endoceras/endoceras_baby.png");
    private static final String BABY_ANIM = "animations/entity/endoceras_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/endoceras_baby.geo.json";
    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/endoceras/endoceras.png");

    public EndocerasModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "endoceras"));
    }

    @Override
    public ResourceLocation getTextureResource(Endoceras animatable) {
        return animatable.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(Endoceras animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO) : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Endoceras animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM) : super.getAnimationResource(animatable);
    }
}
