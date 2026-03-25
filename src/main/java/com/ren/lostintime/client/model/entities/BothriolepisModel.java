package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Bothriolepis;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BothriolepisModel extends DefaultedEntityGeoModel<Bothriolepis> {

    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/bothriolepis_baby.png");
    private static final String BABY_ANIM = "animations/entity/bothriolepis_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/bothriolepis_baby.geo.json";

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/bothriolepis.png");

    public BothriolepisModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "bothriolepis"));
    }

    @Override
    public ResourceLocation getTextureResource(Bothriolepis animatable) {
        return animatable.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;
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
