package com.ren.lostintime.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ren.lostintime.client.model.DodoModel;
import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DodoRender extends GeoEntityRenderer<Dodo> {

    public DodoRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DodoModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public float getMotionAnimThreshold(Dodo animatable) {
        return 1.0E-6F;
    }

    @Override
    public void render(Dodo entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else {
            poseStack.scale(1.0F, 1.0F, 1.0F);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(Dodo animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
