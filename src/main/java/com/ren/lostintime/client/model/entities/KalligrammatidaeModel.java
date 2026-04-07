package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Kalligrammatidae;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class KalligrammatidaeModel extends DefaultedEntityGeoModel<Kalligrammatidae> {

    private static final ResourceLocation VARIANT_1= ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/kalligrammatidae/kalligrammatid_kalligramma.png");
    private static final ResourceLocation VARIANT_2 = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/kalligrammatidae/kalligrammatid_makarkinia.png");
    private static final ResourceLocation VARIANT_3 = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/kalligrammatidae/kalligrammatid_oregramma.png");

    public KalligrammatidaeModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "kalligrammatidae"));
    }

    @Override
    public ResourceLocation getTextureResource(Kalligrammatidae animatable) {
        return switch (animatable.getVariant()) {
            case 1 -> VARIANT_3;
            case 2 -> VARIANT_2;
            default -> VARIANT_1;
        };
    }
}
