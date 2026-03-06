package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Mastodonsaurus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MastodonsaurusModel extends DefaultedEntityGeoModel<Mastodonsaurus> {

    private static final String BABY_TEXTURE = "textures/entity/mastodonsaurus/mastodonsaurus_baby.png";
    private static final String BABY_ANIM = "animations/entity/mastodonsaurus_baby.animation.json";
    private static final String BABY_GEO = "geo/entity/mastodonsaurus_baby.geo.json";
    private static final String YOUNG_TEXTURE = "textures/entity/mastodonsaurus/mastodonsaurus_young.png";
    private static final String YOUNG_ANIM = "animations/entity/mastodonsaurus_young.animation.json";
    private static final String YOUNG_GEO = "geo/entity/mastodonsaurus_young.geo.json";
    private static final String ADULT_TEXTURE = "textures/entity/mastodonsaurus/mastodonsaurus.png";

    public MastodonsaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "mastodonsaurus"));
    }

    @Override
    public ResourceLocation getModelResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_GEO);
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, YOUNG_GEO);
            default -> super.getModelResource(animatable);
        };
    }

    @Override
    public ResourceLocation getTextureResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_TEXTURE);
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, YOUNG_TEXTURE);
            default -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, ADULT_TEXTURE);
        };
    }

    @Override
    public ResourceLocation getAnimationResource(Mastodonsaurus animatable) {
        int stage = animatable.getGrowthStage();

        return switch (stage) {
            case 2 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, BABY_ANIM);
            case 1 -> ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, YOUNG_ANIM);
            default -> super.getAnimationResource(animatable);
        };
    }
}
