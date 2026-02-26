package com.ren.lostintime.client.render.entity.skeleton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.model.ModModelLayers;
import com.ren.lostintime.client.model.skeleton.DodoSkeleton;
import com.ren.lostintime.common.entity.skeleton.FossilEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SkeletonRenderer extends EntityRenderer<FossilEntity> {

    private final EntityModel<FossilEntity> dodoModel;

    public SkeletonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dodoModel = new DodoSkeleton<>(pContext.bakeLayer(ModModelLayers.DODO_LAYER));
        this.shadowRadius = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(FossilEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/skeleton/" + pEntity.getSkeletonType() + "_skeleton.png");
    }

    @Override
    public void render(FossilEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0.0F, -1.5F, 0.0F);

        ResourceLocation texture = getTextureLocation(pEntity);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(dodoModel.renderType(texture));

        dodoModel.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
}
