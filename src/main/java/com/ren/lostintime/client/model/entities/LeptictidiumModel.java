package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Leptictidium;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LeptictidiumModel extends DefaultedEntityGeoModel<Leptictidium> {

    private static final String BABY_TEXTURE = "textures/entity/leptictidium_baby.png";
    private static final String BABY_ANIM = "animations/entity/leptictidium_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/leptictidium_baby.geo.json";

    public LeptictidiumModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "leptictidium"));
    }

    @Override
    public ResourceLocation getModelResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO);
        }
        return super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE);
        }
        return super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM);
        }
        return super.getAnimationResource(animatable);
    }
}
