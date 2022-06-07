package net.permutated.exmachinis.machines.compactor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;
import net.permutated.exmachinis.util.Constants;

import static net.permutated.exmachinis.util.TranslationKey.translateGui;

public class FluxCompactorScreen extends AbstractMachineScreen<AbstractMachineMenu> {
    public FluxCompactorScreen(AbstractMachineMenu container, Inventory inv, Component name) {
        super(container, inv, name, Constants.FLUX_COMPACTOR);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        drawText(matrixStack, translateGui("effects"), 42);
    }
}
