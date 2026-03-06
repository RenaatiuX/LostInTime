package com.ren.lostintime.client.renderer.entity.misc;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.misc.LITBoat;
import com.ren.lostintime.common.entity.misc.LITChestBoat;
import com.ren.lostintime.common.entity.util.BoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

public class LITBoatRenderer extends BoatRenderer {

    private final Map<BoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public LITBoatRenderer(EntityRendererProvider.Context pContext, boolean pChestBoat) {
        super(pContext, pChestBoat);
        this.boatResources = Stream.of(BoatType.values()).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, getTextureLocation(type,
                        pChestBoat)), this.createBoatModel(pContext, type, pChestBoat))));
    }

    private static String getTextureLocation(BoatType pType, boolean pChestBoat) {
        return pChestBoat ? "textures/entity/chest_boat/" + pType.getName() + ".png" : "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, BoatType pType, boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = pChestBoat ? LITBoatRenderer.createChestBoatModelName(pType) : LITBoatRenderer.createBoatModelName(pType);
        ModelPart modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(BoatType pType) {
        return createLocation("boat/" + pType.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(BoatType pType) {
        return createLocation("chest_boat/" + pType.getName(), "main");
    }

    private static ModelLayerLocation createLocation(String pPath, String pModel) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, pPath), pModel);
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if(boat instanceof LITBoat litBoat) {
            return this.boatResources.get(litBoat.getBoatVariant());
        } else if(boat instanceof LITChestBoat litChestBoat) {
            return this.boatResources.get(litChestBoat.getBoatVariant());
        } else {
            return null;
        }
    }
}
