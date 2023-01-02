package net.permutated.exmachinis.machines.crucible;

import net.minecraftforge.fluids.FluidStack;

public class FluidHolderServer implements FluidHolder {
    final FluxCrucibleTile blockEntity;
    public FluidHolderServer(FluxCrucibleTile blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public FluidStack getFluidStack() {
        return blockEntity.getFluidStack();
    }
}
