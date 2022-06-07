package net.permutated.exmachinis.machines.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.util.ResourceUtil;
import net.permutated.exmachinis.util.TextureHolder;

import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateGui;

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



        // texture offset - addon texture

        float energyFraction = this.menu.getEnergyFraction();
        var energyHolder = new TextureHolder(152, 17, 176, 0, 16, 52);
        blit(matrixStack,
            relX + energyHolder.progressOffsetX(),
            relY + energyHolder.progressHeightOffset(energyFraction),
            energyHolder.textureOffsetX(),
            energyHolder.textureOffsetY(),
            energyHolder.textureWidth(),
            energyHolder.getHeightFraction(energyFraction)
        );

        // work progress
        float workFraction = this.menu.getWorkFraction();
        var progressHolder = new TextureHolder(134, 17, 192, 0, 16, 52);
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
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        drawText(matrixStack, this.menu.getWorkStatus().getTranslation(), 72);
    }


    protected void drawText(PoseStack stack, Component component, int yPos) {
        this.font.draw(stack, component, 8, yPos, 4210752);
    }

    @Override
    protected void renderTooltip(PoseStack stack, int x, int y) {
        super.renderTooltip(stack, x, y);

        if (this.isHovering(152, 17, 16, 52, x, y)) {
            this.renderComponentTooltip(stack, List.of(new TextComponent("RF")), x, y, this.font);
        }

        if (this.isHovering(134, 17, 16, 52, x, y)) {
            this.renderComponentTooltip(stack, List.of(new TextComponent("Work")), x, y, this.font);
        }

        if (this.isHovering(116, 53, 16, 16, x, y) && this.menu.getCarried().isEmpty()) {
            this.renderComponentTooltip(stack, List.of(new TextComponent("Upgrade")), x, y, this.font);
        }

        if (this.isHovering(80, 36, 16, 16, x, y) && this.menu.enableMeshSlot && this.menu.getCarried().isEmpty()) {
            this.renderComponentTooltip(stack, List.of(new TextComponent("Mesh")), x, y, this.font);
        }
    }
}
