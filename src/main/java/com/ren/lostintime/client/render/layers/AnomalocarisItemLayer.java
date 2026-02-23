package com.ren.lostintime.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ren.lostintime.common.entity.creatures.Anomalocaris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

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
        poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot)));
        poseStack.mulPose(Axis.XP.rotationDegrees(animatable.getViewXRot(partialTick)));
        poseStack.translate(0.0F, -0.3F, 0.4F);
        poseStack.scale(0.9F, 0.9F, 0.9F);
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable.getHeldItem(), ItemDisplayContext.GROUND, packedLight,
                packedOverlay, poseStack, bufferSource, animatable.level(), 0);
        poseStack.popPose();
    }
}
