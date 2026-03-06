package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Hylonomus;
import com.ren.lostintime.common.entity.enums.HylonomusVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HylonomusModel extends DefaultedEntityGeoModel<Hylonomus> {

    private static final String ADULT_TEXTURE = "textures/entity/hylonomus/adult/";
    private static final String BABY_TEXTURE = "textures/entity/hylonomus/baby/";
    private static final String BABY_ANIM = "animations/entity/hylonomus_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/hylonomus_baby.geo.json";

    public HylonomusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "hylonomus"));
    }

    @Override
    public ResourceLocation getModelResource(Hylonomus animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO);
        }
        return super.getModelResource(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(Hylonomus animatable) {
        if (animatable.isBaby()) {
            return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM);
        }
        return super.getAnimationResource(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(Hylonomus animatable) {
        HylonomusVariant variant = animatable.getVariant();

        String folder = animatable.isBaby() ? BABY_TEXTURE : ADULT_TEXTURE;

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
