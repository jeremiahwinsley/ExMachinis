package net.permutated.exmachinis.machines.crucible;

import net.minecraftforge.fluids.FluidStack;

public class FluidHolderClient implements FluidHolder {
    private FluidStack fluidStack = FluidStack.EMPTY;

    @Override
    public FluidStack getFluidStack() {
        return fluidStack;
    }

    @Override
    public void setFluidStack(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }
}
