package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Mastodonsaurus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MastodonsaurusModel extends DefaultedEntityGeoModel<Mastodonsaurus> {

    public MastodonsaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "mastodonsaurus"));
    }

    @Override
    public ResourceLocation getModelResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus_baby.geo.json");
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus_young.geo.json");
            default -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus.geo.json");
        };
    }

    @Override
    public ResourceLocation getTextureResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus_baby.png");
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus_young.png");
            default -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus.png");
        };
    }

    @Override
    public ResourceLocation getAnimationResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus_baby.animation.json");
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus_young.animation.json");
            default -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus.animation.json");
        };
    }
}
