package net.permutated.exmachinis.util;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.items.ItemHandlerHelper.copyStackWithSize;

public class ItemStackUtil {
    private ItemStackUtil() {
        // nothing to do
    }

    /**
     * Multiply input stack, returning a List of one or more resulting ItemStacks.
     * @param input the input stack to copy.
     * @param times the multiplier
     * @return the list of ItemStacks
     */
    public static List<ItemStack> multiplyStack(ItemStack input, int times) {
        int count = input.getCount();
        int max = input.getMaxStackSize();

        int amount = count * times;
        List<ItemStack> output = new ArrayList<>();
        while (amount > max) {
            output.add(copyStackWithSize(input, max));
            amount -= max;
        }
        output.add(copyStackWithSize(input, amount));
        return output;
    }
}
