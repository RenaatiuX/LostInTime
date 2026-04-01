package com.ren.lostintime.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class LITEyesLayer <T extends GeoAnimatable> extends GeoRenderLayer<T> {

    private final ResourceLocation eyesTexture;
    private final boolean glowInNight;

    public LITEyesLayer(GeoRenderer<T> entityRendererIn, ResourceLocation eyesTexture, boolean glowInNight) {
        super(entityRendererIn);
        this.eyesTexture = eyesTexture;
        this.glowInNight = glowInNight;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        boolean shouldRender = true;
        if (this.glowInNight) {
            if (animatable instanceof Entity entity) {
                shouldRender = entity.level().isNight();
            }
        }
        if (shouldRender) {
            RenderType emissiveRenderType = RenderType.eyes(this.eyesTexture);
            getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
                    bufferSource.getBuffer(emissiveRenderType), partialTick,
                    15728640,
                    OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
