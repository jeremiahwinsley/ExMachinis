package net.permutated.exmachinis.machines.crucible;

import net.minecraftforge.fluids.FluidStack;

public interface FluidHolder {
    FluidStack getFluidStack();
    default void setFluidStack(FluidStack fluidStack) {}
}
