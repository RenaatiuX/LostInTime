package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Leptictidium;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LeptictidiumModel extends DefaultedEntityGeoModel<Leptictidium> {

    public LeptictidiumModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "leptictidium"));
    }

    @Override
    public ResourceLocation getModelResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/leptictidium_baby.geo.json");
        }
        return super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/leptictidium_baby.png");
        }
        return super.getTextureResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Leptictidium animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/leptictidium_baby.animation.json");
        }
        return super.getAnimationResource(animatable);
    }
}
