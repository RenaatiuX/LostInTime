package com.ren.lostintime.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ren.lostintime.common.entity.util.IBloodyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class LITBloodLayer <T extends GeoAnimatable & IBloodyEntity> extends GeoRenderLayer<T> {

    private final ResourceLocation bloodTexture;

    public LITBloodLayer(GeoRenderer<T> entityRendererIn, ResourceLocation bloodTexture) {
        super(entityRendererIn);
        this.bloodTexture = bloodTexture;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.getBloodTimer() > 0) {

            RenderType bloodRenderType = RenderType.entityCutout(this.bloodTexture);

            getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, bloodRenderType,
                    bufferSource.getBuffer(bloodRenderType), partialTick,
                    packedLight,
                    packedOverlay,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
