package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Daeodon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaeodonModel extends DefaultedEntityGeoModel<Daeodon> {

    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/daeodon/daeodon_baby.png");
    private static final String BABY_ANIM = "animations/entity/daeodon_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/daeodon_baby.geo.json";

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/daeodon.png");


    public DaeodonModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "daeodon"));
    }

    @Override
    public ResourceLocation getTextureResource(Daeodon animatable) {
        return animatable.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(Daeodon animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO) : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Daeodon animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM) : super.getAnimationResource(animatable);
    }
}
