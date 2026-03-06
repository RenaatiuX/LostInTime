package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Daeodon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaeodonModel extends DefaultedEntityGeoModel<Daeodon> {

    private static final String BABY_TEXTURE = "textures/entity/daeodon/daeodon_baby.png";
    private static final String BABY_ANIM = "animations/entity/daeodon_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/daeodon_baby.geo.json";

    public DaeodonModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "daeodon"));
    }

    @Override
    public ResourceLocation getTextureResource(Daeodon animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE) : super.getTextureResource(animatable);
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
