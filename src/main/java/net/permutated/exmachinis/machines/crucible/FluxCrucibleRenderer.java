package net.permutated.exmachinis.machines.crucible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.permutated.exmachinis.util.FluidStackUtil;
import net.permutated.exmachinis.util.RenderUtil;

public class FluxCrucibleRenderer implements BlockEntityRenderer<FluxCrucibleTile> {
    @SuppressWarnings("unused")
    public FluxCrucibleRenderer(BlockEntityRendererProvider.Context context) {
        // nothing to do
    }

    @Override
    public void render(FluxCrucibleTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        var fluidStack = tile.getFluidStack();
        if (fluidStack.isEmpty()) {
            return;
        }

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());

        TextureAtlasSprite sprite = RenderUtil.getFluidTexture(fluidStack);
        int color = RenderUtil.getFluidColor(fluidStack);
        float percent = FluidStackUtil.getFluidPercentage(fluidStack, tile.getFluidCapacity());
        float adjusted = (0.75f * percent) - 0.005f;

        poseStack.pushPose();
        poseStack.translate(.5, .5, .5);
        poseStack.translate(-.5, -.5, -.5);

        int u = 0;
        int v = 1;

        builder
            .vertex(poseStack.last().pose(), u, 0.25f + adjusted, v)
            .color(color)
            .uv(sprite.getU0(), sprite.getV1())
            .uv2(combinedLight)
            .normal(1, 0, 0)
            .endVertex();

        builder
            .vertex(poseStack.last().pose(), v, 0.25f + adjusted, v)
            .color(color)
            .uv(sprite.getU1(), sprite.getV1())
            .uv2(combinedLight)
            .normal(1, 0, 0)
            .endVertex();

        builder
            .vertex(poseStack.last().pose(), v, 0.25f + adjusted, u)
            .color(color)
            .uv(sprite.getU1(), sprite.getV0())
            .uv2(combinedLight)
            .normal(1, 0, 0)
            .endVertex();

        builder
            .vertex(poseStack.last().pose(), u, 0.25f + adjusted, u)
            .color(color)
            .uv(sprite.getU0(), sprite.getV0())
            .uv2(combinedLight)
            .normal(1, 0, 0)
            .endVertex();

        poseStack.popPose();
    }
}
