package net.permutated.exmachinis.machines.buffer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.machines.base.AbstractMachineScreen;
import net.permutated.exmachinis.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class ItemBufferScreen extends AbstractMachineScreen<ItemBufferMenu> {
    public ItemBufferScreen(ItemBufferMenu container, Inventory inv, Component name) {
        super(container, inv, name, Constants.ITEM_BUFFER);
    }
}
