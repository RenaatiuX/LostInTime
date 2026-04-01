package com.ren.lostintime.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ren.lostintime.client.model.entities.PlesiosaurusModel;
import com.ren.lostintime.common.entity.creatures.Plesiosaurus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlesiosaurusRenderer extends GeoEntityRenderer<Plesiosaurus> {

    public PlesiosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new PlesiosaurusModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public float getMotionAnimThreshold(Plesiosaurus animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Plesiosaurus animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    protected void applyRotations(Plesiosaurus animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        float pitch = Mth.lerp(partialTick, animatable.prevSwimPitch, animatable.swimPitch);
        pitch = -pitch;

        float halfHeight = animatable.getBbHeight() * 0.5F;
        poseStack.translate(0.0D, halfHeight, 0.0D);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        poseStack.translate(0.0D, -halfHeight, 0.0D);
    }
}
