package net.permutated.exmachinis.machines.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.util.ResourceUtil;
import net.permutated.exmachinis.util.TextureHolder;
import net.permutated.exmachinis.util.WorkStatus;

import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation gui;

    protected AbstractMachineScreen(T container, Inventory inv, Component name, String machine) {
        super(container, inv, name);
        this.gui = ResourceUtil.gui(machine);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, gui);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);


        // render work status texture
        if (this.menu.getWorkStatus() == WorkStatus.WORKING) {
            this.blit(matrixStack, relX + 116, relY + 36, 224, 0, 16, 16);
        } else if(this.menu.getWorkStatus() == WorkStatus.REDSTONE_DISABLED) {
            this.blit(matrixStack, relX + 116, relY + 36, 240, 0, 16, 16);
        } else {
            this.blit(matrixStack, relX + 116, relY + 36, 208, 0, 16, 16);
        }

        // texture offset - addon texture

        float energyFraction = this.menu.dataHolder.getEnergyFraction();
        var energyHolder = new TextureHolder(152, 18, 176, 0, 16, 52);
        blit(matrixStack,
            relX + energyHolder.progressOffsetX(),
            relY + energyHolder.progressHeightOffset(energyFraction),
            energyHolder.textureOffsetX(),
            energyHolder.textureOffsetY(),
            energyHolder.textureWidth(),
            energyHolder.getHeightFraction(energyFraction)
        );

        // work progress
        float workFraction = this.menu.dataHolder.getWorkFraction();
        var progressHolder = new TextureHolder(134, 18, 192, 0, 16, 52);
        blit(matrixStack,
            relX + progressHolder.progressOffsetX(),
            relY + progressHolder.progressHeightOffset(workFraction),
            progressHolder.textureOffsetX(),
            progressHolder.textureOffsetY(),
            progressHolder.textureWidth(),
            progressHolder.getHeightFraction(workFraction)
        );
    }

    @Override
    protected void renderTooltip(PoseStack stack, int x, int y) {
        super.renderTooltip(stack, x, y);

        if (this.isHovering(116, 36, 16, 16, x, y)) {
            this.renderComponentTooltip(stack, List.of(this.menu.getWorkStatus().getTranslation()), x, y, this.font);
        }

        if (this.isHovering(152, 18, 16, 52, x, y)) {
            this.renderComponentTooltip(stack, List.of(
                translateTooltip("fluxBar"),
                translateTooltip("fluxData", this.menu.dataHolder.getEnergy(), this.menu.dataHolder.getMaxEnergy())
            ), x, y, this.font);
        }

        if (this.isHovering(134, 18, 16, 52, x, y)) {
            this.renderComponentTooltip(stack, List.of(
                translateTooltip("workBar"),
                translateTooltip("workData", this.menu.dataHolder.getWork(), this.menu.dataHolder.getMaxWork())
            ), x, y, this.font);
        }

        if (this.isHovering(116, 54, 16, 16, x, y) && shouldShowSlotTooltip()) {
            this.renderComponentTooltip(stack, List.of(translateTooltip("upgradeSlot")), x, y, this.font);
        }

        if (this.isHovering(80, 36, 16, 16, x, y) && this.menu.enableMeshSlot && shouldShowSlotTooltip()) {
            this.renderComponentTooltip(stack, List.of(translateTooltip("meshSlot")), x, y, this.font);
        }
    }

    private boolean shouldShowSlotTooltip() {
        return this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem();
    }
}
