package net.permutated.exmachinis.machines.compactor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;

public class FluxCompactorMenu extends AbstractMachineMenu {

    public FluxCompactorMenu(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.FLUX_COMPACTOR_MENU.get(), windowId, playerInventory, packetBuffer);

    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.FLUX_COMPACTOR_BLOCK;
    }
}
