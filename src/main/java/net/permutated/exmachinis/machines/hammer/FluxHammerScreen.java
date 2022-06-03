package net.permutated.exmachinis.machines.hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;

import static net.permutated.exmachinis.util.TranslationKey.translateGui;

@SuppressWarnings("java:S110") // inheritance required
public class FluxHammerScreen extends AbstractMachineScreen<AbstractMachineMenu> {
    public FluxHammerScreen(AbstractMachineMenu container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
    }
}
