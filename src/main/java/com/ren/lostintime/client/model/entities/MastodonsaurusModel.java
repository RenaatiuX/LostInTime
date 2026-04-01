package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.creatures.Mastodonsaurus;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MastodonsaurusModel extends DefaultedEntityGeoModel<Mastodonsaurus> {

    private static final ResourceLocation ADULT_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus.geo.json");
    private static final ResourceLocation YOUNG_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus_young.geo.json");
    private static final ResourceLocation BABY_MODEL = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "geo/entity/mastodonsaurus_tadpole.geo.json");

    private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus/mastodonsaurus.png");
    private static final ResourceLocation YOUNG_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus/mastodonsaurus_young.png");
    private static final ResourceLocation BABY_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/mastodonsaurus/mastodonsaurus_baby.png");

    private static final ResourceLocation ADULT_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus.animation.json");
    private static final ResourceLocation YOUNG_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus_young.animation.json");
    private static final ResourceLocation BABY_ANIM = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "animations/entity/mastodonsaurus_baby.animation.json");

    public MastodonsaurusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "mastodonsaurus"));
    }

    @Override
    public ResourceLocation getModelResource(Mastodonsaurus animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_MODEL;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_MODEL;
        } else {
            return ADULT_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(Mastodonsaurus animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_TEXTURE;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_TEXTURE;
        } else {
            return ADULT_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(Mastodonsaurus animatable) {
        GrowthStage growthStage = animatable.getGrowthStage();
        if (growthStage == GrowthStage.BABY) {
            return BABY_ANIM;
        } else if (growthStage == GrowthStage.JUVENILE) {
            return YOUNG_ANIM;
        } else {
            return ADULT_ANIM;
        }
    }
}
