package net.permutated.exmachinis.compat.exnihilo;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import novamachina.exnihilosequentia.common.item.HammerBaseItem;
import novamachina.exnihilosequentia.common.item.MeshItem;
import novamachina.exnihilosequentia.common.registries.ExNihiloRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Access class for Ex Nihilo Sequentia so that external code isn't referenced from the rest of the mod.
 */
public class ExNihiloAPI {
    private ExNihiloAPI() {
        // nothing to do
    }

    private static Optional<Block> blockFromItemStack(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            return Optional.of(blockItem.getBlock());
        }
        return Optional.empty();
    }

    public static boolean canHammer(ItemStack stack) {
        return blockFromItemStack(stack)
            .map(ExNihiloRegistries.HAMMER_REGISTRY::isHammerable)
            .orElse(false);
    }

    public static List<ItemStack> getHammerResult(ItemStack stack) {
        return blockFromItemStack(stack)
            .map(ExNihiloRegistries.HAMMER_REGISTRY::getResult)
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(chance -> chance.getChance() == 1.0 || ThreadLocalRandom.current().nextFloat() <= chance.getChance())
            .map(chance -> chance.getStack().copy())
            .toList();
    }

    public static boolean isMeshItem(ItemStack stack) {
        return stack.getItem() instanceof MeshItem;
    }

    public static boolean isHammerItem(ItemStack stack) {
        return stack.getItem() instanceof HammerBaseItem;
    }

    public static boolean canSieve(ItemStack stack, ItemStack mesh, boolean waterlogged) {
        if (mesh.getItem() instanceof MeshItem meshItem) {
            return blockFromItemStack(stack)
                .map(block -> ExNihiloRegistries.SIEVE_REGISTRY.isBlockSiftable(block, meshItem.getType(), waterlogged))
                .orElse(false);
        }
        return false;
    }

    public static List<ItemStack> getSieveResult(ItemStack stack, ItemStack mesh, boolean waterlogged) {
        if (mesh.getItem() instanceof MeshItem meshItem) {
            var recipes = ExNihiloRegistries.SIEVE_REGISTRY.getDrops(stack.getItem(), meshItem.getType(), waterlogged);
            int fortune = mesh.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);

            List<ItemStack> output = new ArrayList<>();
            for (var sieveRecipe : recipes) {
                for (var roll : sieveRecipe.getRolls()) {
                    if (ThreadLocalRandom.current().nextFloat() <= roll.getChance() * (1F + fortune / 3F)) {
                        output.add(sieveRecipe.getDrop());
                    }
                }
            }
            return output;
        }
        return Collections.emptyList();
    }

}
