package net.permutated.exmachinis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleMenu;
import net.permutated.exmachinis.util.ClientUtil;

import java.util.function.Supplier;

public class PacketFluidSync {
    private final FluidStack fluidStack;
    private final int containerId;

    public PacketFluidSync(FluidStack fluidStack, int containerId) {
        this.fluidStack = fluidStack.copy();
        this.containerId = containerId;
    }

    public PacketFluidSync(FriendlyByteBuf buffer) {
        this.fluidStack = buffer.readFluidStack();
        this.containerId = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeFluidStack(fluidStack);
        buffer.writeInt(containerId);
    }

    @SuppressWarnings("java:S1172")
    public static void handle(PacketFluidSync event, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ClientUtil.getClientPlayer();
            if (player != null
                && player.containerMenu instanceof FluxCrucibleMenu fluxCrucibleMenu
                && fluxCrucibleMenu.containerId == event.containerId) {
                fluxCrucibleMenu.fluidHolder.setFluidStack(event.fluidStack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
