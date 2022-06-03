package net.permutated.exmachinis.machines.sieve;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;

public class FluxSieveMenu extends AbstractMachineMenu {

    public FluxSieveMenu(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.FLUX_SIEVE_MENU.get(), windowId, playerInventory, packetBuffer);

    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.FLUX_SIEVE_BLOCK;
    }
}
