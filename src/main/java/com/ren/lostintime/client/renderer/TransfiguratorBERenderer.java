package com.ren.lostintime.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.model.ModModelLayers;
import com.ren.lostintime.client.model.blockentities.TransfiguratorEgg2;
import com.ren.lostintime.client.model.blockentities.TransfiguratorEmbryo;
import com.ren.lostintime.client.model.blockentities.TransfiguratorPlant;
import com.ren.lostintime.common.block.TransfiguratorBlock;
import com.ren.lostintime.common.blockentity.TransfiguratorBE;
import com.ren.lostintime.common.recipe.TransfiguratorRecipe;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class TransfiguratorBERenderer implements BlockEntityRenderer<TransfiguratorBE> {

    private final TransfiguratorEgg2 eggModel;
    private final TransfiguratorEmbryo embryoModel;
    private final TransfiguratorPlant plantModel;

    public TransfiguratorBERenderer(BlockEntityRendererProvider.Context context) {
        this.eggModel = new TransfiguratorEgg2(context.bakeLayer(ModModelLayers.TRANSFIGURATOR_EGG_2));
        this.embryoModel = new TransfiguratorEmbryo(context.bakeLayer(ModModelLayers.TRANSFIGURATOR_EMBRYO));
        this.plantModel = new TransfiguratorPlant(context.bakeLayer(ModModelLayers.TRANSFIGURATOR_PLANT));
    }

    @Override
    public void render(TransfiguratorBE pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // Only render on the lower half to avoid duplication
        if (pBlockEntity.getLevel() == null || pBlockEntity.getBlockState().getValue(TransfiguratorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }

        TransfiguratorRecipe.Type type = pBlockEntity.getCurrentType();
        if (type == null) return;

        pPoseStack.pushPose();
        
        // Center the render in the machine (adjust Y as needed, 1.25 puts it in the upper glass area)
        // Rotate to match block facing
        Direction facing = pBlockEntity.getBlockState().getValue(TransfiguratorBlock.FACING);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        switch (type) {
            case EGG:
                pPoseStack.translate(0.5, -1.2, 0.5);
                pPoseStack.translate(0, Mth.sin(pBlockEntity.getLevel().getGameTime() * 0.05f) * 0.05f, 0);
                pPoseStack.scale(1.5f, 1.5f, 1.5f);
                renderModel(this.eggModel, TransfiguratorEgg2.TEXTURE, pPoseStack, pBufferSource, pPackedLight);
                break;
            case EMBRYO:
                pPoseStack.translate(0.5f, -0.2f, 0.5f);
                pPoseStack.translate(0, Mth.sin(pBlockEntity.getLevel().getGameTime() * 0.05f) * 0.02f, 0);
                renderModel(this.embryoModel, TransfiguratorEmbryo.TEXTURE, pPoseStack, pBufferSource, pPackedLight);
                break;
            case PLANT:
                pPoseStack.scale(2.5f, 2.5f, 2.5f);
                pPoseStack.translate(0.2, -1.0f, 0.2);
                pPoseStack.translate(0, Mth.sin(pBlockEntity.getLevel().getGameTime() * 0.05f) * 0.02f, 0);
                renderModel(this.plantModel, TransfiguratorPlant.TEXTURE, pPoseStack, pBufferSource, pPackedLight);
                break;
        }

        pPoseStack.popPose();
    }

    private void renderModel(Model model, ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(model.renderType(texture));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}