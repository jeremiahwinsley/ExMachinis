package net.permutated.exmachinis.machines.compactor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;
import net.permutated.exmachinis.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class FluxCompactorScreen extends AbstractMachineScreen<AbstractMachineMenu> {
    public FluxCompactorScreen(AbstractMachineMenu container, Inventory inv, Component name) {
        super(container, inv, name, Constants.FLUX_COMPACTOR);
    }
}
