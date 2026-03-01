package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Hylonomus;
import com.ren.lostintime.common.entity.enums.HylonomusVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HylonomusModel extends DefaultedEntityGeoModel<Hylonomus> {

    public HylonomusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "hylonomus"));
    }

    @Override
    public ResourceLocation getModelResource(Hylonomus animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/hylonomus_baby.geo.json");
        }
        return super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Hylonomus animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/hylonomus_baby.animation.json");
        }
        return super.getAnimationResource(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(Hylonomus animatable) {
        HylonomusVariant variant = animatable.getVariant();

        String folder = animatable.isBaby() ? "textures/entity/hylonomus/baby/" : "textures/entity/hylonomus/adult/";

        return switch (variant) {
            case LEAF -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "leaf.png");
            case ROCK -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "rock.png");
            case RUSTY -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "rusty.png");
            case SPOTTED -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "spotted.png");
            case STELAR -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "stelar.png");
            case STRIPPED -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, folder + "stripped.png");
        };
    }
}
