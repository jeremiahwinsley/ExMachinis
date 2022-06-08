package net.permutated.exmachinis.machines.sieve;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;
import net.permutated.exmachinis.util.Constants;

import static net.permutated.exmachinis.util.TranslationKey.translateGui;

public class FluxSieveScreen extends AbstractMachineScreen<AbstractMachineMenu> {
    public FluxSieveScreen(AbstractMachineMenu container, Inventory inv, Component name) {
        super(container, inv, name, Constants.FLUX_SIEVE);
    }
}
