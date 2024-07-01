package com.leo.voidminers.block.entity.renderer;

import com.leo.voidminers.block.entity.ControllerBaseBE;
import com.leo.voidminers.util.MiscUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.List;

public class ControllerRenderer implements BlockEntityRenderer<ControllerBaseBE> {

    public ControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ControllerBaseBE pBlockEntity, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.active) {
            long gameTime = pBlockEntity.getLevel().getGameTime();
            float f = (float) Math.floorMod(gameTime, 40) + pPartialTick;

            pose.pushPose();
            /* Partially copied from the beacon renderer */
            pose.translate(0.5f, 0, 0.5f);
            pose.mulPose(Axis.YP.rotationDegrees(f * 2.25f - 45f));

            renderBeam(pBuffer.getBuffer(RenderType.gui()), pose, new Vector3f(0f, 0f, 0f), pBlockEntity.getBeamColor(), 320, 0.3f);

            pose.popPose();
        } else if (pBlockEntity.showStructure) {
            String structure = pBlockEntity.getStructure().toString();

            if (MiscUtil.structureMap.containsKey(structure)) {
                int offset = MiscUtil.structureMap.get(structure).get(0).size() / 2;

                pose.pushPose();
                pose.translate(-offset, 1, -offset);
                pose.pushPose();
                pose.mulPose(Axis.ZN.rotationDegrees(90));

                //TODO Find a better way to do this, it's performance intensive doing 3 loops each render tick
                List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structure);

                for (int x = 0; x < blocks.size(); x++) {
                    List<List<BlockState>> b2 = blocks.get(x);

                    for (int y = 0; y < b2.size(); y++) {
                        List<BlockState> b3 = b2.get(y);

                        for (int z = 0; z < b3.size(); z++) {
                            BlockState block = b3.get(z);

                            pose.pushPose();
                            pose.translate(x, y, z);

                            renderBlock(
                                block,
                                pose,
                                pBuffer
                            );

                            pose.popPose();
                        }
                    }
                }

                pose.popPose();
                pose.popPose();
            }
        }
    }

    public void renderBlock(BlockState state, PoseStack pose, MultiBufferSource buffer) {
        Minecraft minecraft = Minecraft.getInstance();

        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();

        blockRenderer.renderSingleBlock(
            state,
            pose,
            buffer,
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            blockRenderer.getBlockModel(state).getModelData(minecraft.level, new BlockPos(0, 0, 0), state, ModelData.EMPTY),
            RenderType.translucent()
        );
    }

    public void renderBeam(VertexConsumer vC, PoseStack pose, Vector3f center, int color, float length, float width) {
        for (int i = 0; i < 4; i++) {
            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(90 * i));
            pose.pushPose();
            pose.translate(-width / 2, 0, -width / 2);
            renderQuad(vC, pose, center, color, length, width);
            pose.popPose();
            pose.popPose();
        }
    }

    public void renderQuad(VertexConsumer vC, PoseStack pose, Vector3f pos, int color, float length, float width) {
        vC.vertex(pose.last().pose(), pos.x, pos.y, pos.z)
            .color(color)
            .uv(0, 0)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0, 0, 0)
            .endVertex();

        vC.vertex(pose.last().pose(), pos.x + width, pos.y, pos.z)
            .color(color)
            .uv(1, 0)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0, 0, 0)
            .endVertex();

        vC.vertex(pose.last().pose(), pos.x + width, pos.y - length, pos.z)
            .color(color)
            .uv(1, 1)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0, 0, 0)
            .endVertex();

        vC.vertex(pose.last().pose(), pos.x, pos.y - length, pos.z)
            .color(color)
            .uv(0, 1)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(0, 0, 0)
            .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(ControllerBaseBE pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(ControllerBaseBE pBlockEntity, Vec3 pCameraPos) {
        return pBlockEntity.getBlockPos().getCenter().distanceTo(pCameraPos) <= 100;
    }
}
