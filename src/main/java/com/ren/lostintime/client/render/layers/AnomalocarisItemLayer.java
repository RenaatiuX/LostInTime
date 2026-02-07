package com.ren.lostintime.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ren.lostintime.common.entity.creatures.Anomalocaris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class AnomalocarisItemLayer extends GeoRenderLayer<Anomalocaris> {

    public AnomalocarisItemLayer(GeoRenderer<Anomalocaris> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, Anomalocaris animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.hasHeldItem()) return;
        GeoBone mouthBone = bakedModel.getBone("body").orElse(null);
        if (mouthBone == null) return;
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 0.5F);
        poseStack.translate(0.2F, 0.0F, 1.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable.getHeldItem(), ItemDisplayContext.GROUND, packedLight,
                packedOverlay, poseStack, bufferSource, animatable.level(), 0);
        poseStack.popPose();
    }
}
