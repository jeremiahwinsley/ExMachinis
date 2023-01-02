package net.permutated.exmachinis.machines.crucible;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.network.NetworkDispatcher;
import net.permutated.exmachinis.network.PacketFluidSync;

public class FluxCrucibleMenu extends AbstractMachineMenu {
    private final Player player;
    public final FluidHolder fluidHolder;
    public final int capacity;
    public FluxCrucibleMenu(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.FLUX_CRUCIBLE_MENU.get(), windowId, playerInventory, packetBuffer);
        this.player = playerInventory.player;
        this.capacity = packetBuffer.readInt();

        this.fluidHolder = containerLevelAccess.evaluate((level, pos) -> {
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof FluxCrucibleTile tile) {
                return new FluidHolderServer(tile);
            } else {
                return new FluidHolderClient();
            }
        }, new FluidHolderClient());
    }

    private FluidStack cached = FluidStack.EMPTY;

    @Override
    public void broadcastChanges() {
        FluidStack current = fluidHolder.getFluidStack();
        if (!current.isFluidStackIdentical(cached) && player instanceof ServerPlayer serverPlayer) {
            NetworkDispatcher.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketFluidSync(current, containerId));
            cached = current.copy();
        }

        super.broadcastChanges();
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.FLUX_CRUCIBLE_BLOCK;
    }
}
