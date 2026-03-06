package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Bothriolepis;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BothriolepisModel extends DefaultedEntityGeoModel<Bothriolepis> {

    private static final String BABY_TEXTURE = "textures/entity/bothriolepis_baby.png";
    private static final String BABY_ANIM = "animations/entity/bothriolepis_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/bothriolepis_baby.geo.json";

    public BothriolepisModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "bothriolepis"));
    }

    @Override
    public ResourceLocation getTextureResource(Bothriolepis animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE) : super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getModelResource(Bothriolepis animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO) : super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Bothriolepis animatable) {
        return animatable.isBaby() ? ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM) : super.getAnimationResource(animatable);
    }
}
