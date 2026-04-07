package com.ren.lostintime.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ren.lostintime.client.model.entities.KalligrammatidaeModel;
import com.ren.lostintime.common.entity.creatures.Kalligrammatidae;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KalligrammatidaeRenderer extends GeoEntityRenderer<Kalligrammatidae> {

    public KalligrammatidaeRenderer(EntityRendererProvider.Context context) {
        super(context, new KalligrammatidaeModel());
        this.shadowRadius = 0.2f;
    }

    @Override
    public void preRender(PoseStack poseStack, Kalligrammatidae animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.isLanded()) {
            Direction face = animatable.getAttachFace();

            float bodyYaw = animatable.getViewYRot(partialTick);
            poseStack.mulPose(Axis.YP.rotationDegrees(-bodyYaw));

            switch (face) {
                case DOWN:
                    poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                    poseStack.translate(0.0D, -animatable.getBbHeight(), 0.0D);
                    break;
                case UP:
                    break;
                default:
                    poseStack.mulPose(face.getRotation());
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                    poseStack.translate(0.0D, -0.05D, 0.0D);
                    break;
            }
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
