package net.permutated.exmachinis.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps a given FluidTank and only allows extracting.
 */
public class FluidTankWrapper implements IFluidTank {
    private final FluidTank delegate;

    public FluidTankWrapper(FluidTank delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return delegate.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return delegate.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return delegate.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return delegate.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return delegate.drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return delegate.drain(resource, action);
    }
}
