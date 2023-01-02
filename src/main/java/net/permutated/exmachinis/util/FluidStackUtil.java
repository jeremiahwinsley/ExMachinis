package net.permutated.exmachinis.util;

import com.google.common.math.IntMath;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidStackUtil {
    private FluidStackUtil() {
        // nothing to do
    }

    public static FluidStack multiplyStackCount(FluidStack input, int times) {
        int count = IntMath.saturatedMultiply(input.getAmount(), times);
        return copyStackWithSize(input, count);
    }

    public static FluidStack copyStackWithSize(FluidStack fluidStack, int size) {
        if (size == 0)
            return FluidStack.EMPTY;
        FluidStack copy = fluidStack.copy();
        copy.setAmount(size);
        return copy;
    }

    public static FluidStack insertFluid(IFluidHandler fluidHandler, FluidStack fluidStack, boolean simulate) {
        int filled = fluidHandler.fill(fluidStack, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        int total = fluidStack.getAmount();

        int remaining = Math.max(0, total - filled);
        if (remaining == 0) {
            return FluidStack.EMPTY;
        } else {
            return FluidStackUtil.copyStackWithSize(fluidStack, remaining);
        }
    }

    public static float getFluidPercentage(FluidStack fluidStack, int capacity) {
        if (fluidStack.getAmount() == 0) {
            return 0f;
        } else {
            return ((float) Mth.clamp(fluidStack.getAmount(), 0, capacity)) / capacity;
        }
    }
}
