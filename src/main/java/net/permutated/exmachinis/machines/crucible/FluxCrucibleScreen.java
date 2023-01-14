package net.permutated.exmachinis.machines.crucible;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.FluidStackUtil;
import net.permutated.exmachinis.util.RenderUtil;
import net.permutated.exmachinis.util.TextureHolder;

import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;
import static net.permutated.exmachinis.util.TranslationKey.translateTooltipInt;

@SuppressWarnings("java:S110") // inheritance required
public class FluxCrucibleScreen extends AbstractMachineScreen<FluxCrucibleMenu> {
    public FluxCrucibleScreen(FluxCrucibleMenu container, Inventory inv, Component name) {
        super(container, inv, name, Constants.FLUX_CRUCIBLE);
    }

    public static final TextureHolder tankHolder = new TextureHolder(80, 18, 176, 52, 16, 52);

    @Override
    protected void renderTooltip(PoseStack stack, int x, int y) {
        super.renderTooltip(stack, x, y);
        if (this.isHovering(80, 18, 16, 52, x, y)) {
            FluidStack fluid = this.menu.fluidHolder.getFluidStack();
            this.renderComponentTooltip(stack, List.of(
                fluid.isEmpty() ? translateTooltip("fluidNone") : fluid.getDisplayName(),
                translateTooltipInt("fluidData", fluid.getAmount(), this.menu.getCapacity())
            ), x, y, this.font);
        }
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack stack) {
        List<Component> tooltip = super.getTooltipFromItem(stack);

        if (stack.getItem() instanceof UpgradeItem upgradeItem) {
            var clamped = FluxCrucibleTile.CrucibleTierConfig.INSTANCE.clampTier(upgradeItem.getTier());
            if (clamped != upgradeItem.getTier()) {
                tooltip.add(translateTooltip("clamped", clamped.component()).withStyle(ChatFormatting.GOLD));
            }
        }

        return tooltip;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        FluidStack fluidStack = this.menu.fluidHolder.getFluidStack();
        drawFluid(fluidStack, relX, relY);

        blit(poseStack,
            relX + tankHolder.progressOffsetX(),
            relY + tankHolder.progressOffsetY(),
            tankHolder.textureOffsetX(),
            tankHolder.textureOffsetY(),
            tankHolder.textureWidth(),
            tankHolder.textureHeight()
        );
    }

    // currently assumes a 16x16 fluid texture
    private void drawFluid(FluidStack fluidStack, int relX, int relY) {
        if (fluidStack.isEmpty()) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        TextureAtlasSprite sprite = RenderUtil.getFluidTexture(fluidStack);
        int color = RenderUtil.getFluidColor(fluidStack);
        RenderUtil.setShaderColorFromInt(color);

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        float percentage = FluidStackUtil.getFluidPercentage(fluidStack, this.menu.getCapacity());

        int startX = relX + tankHolder.progressOffsetX();
        int startY = relY + tankHolder.progressHeightOffset(percentage);
        int endX = startX + tankHolder.textureWidth();

        int target = tankHolder.getHeightFraction(percentage);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0;i < target;i += 16) {
            int drawY = startY + i;
            float tileHeight = Math.min(target - i, 16);
            float v = minV + (maxV - minV) * tileHeight / 16F;
            buffer.vertex(startX, drawY + tileHeight, 0).uv(minU, v).endVertex();
            buffer.vertex(endX, drawY + tileHeight, 0).uv(maxU, v).endVertex();
            buffer.vertex(endX, drawY, 0).uv(maxU, minV).endVertex();
            buffer.vertex(startX, drawY, 0).uv(minU, minV).endVertex();
        }

        Tesselator.getInstance().end();
        RenderSystem.disableBlend();
        resetTexture();
    }
}
