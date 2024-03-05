package net.permutated.exmachinis.machines.buffer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;

public class ItemBufferMenu extends AbstractMachineMenu {
    public ItemBufferMenu(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(ModRegistry.ITEM_BUFFER_MENU.get(), windowId, playerInventory, buf);
    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.ITEM_BUFFER_BLOCK;
    }
}
