package net.permutated.exmachinis.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.data.builders.CompactingRecipeBuilder;
import novamachina.exnihilosequentia.common.init.ExNihiloItems;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CompactingRecipes extends RecipeProvider {
    public CompactingRecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_IRON))
            .setInput(Ingredient.of(ExNihiloItems.IRON.getPieceItem()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_GOLD))
            .setInput(Ingredient.of(ExNihiloItems.GOLD.getPieceItem()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_COPPER))
            .setInput(Ingredient.of(ExNihiloItems.COPPER.getPieceItem()), 4)
            .build(consumer);

        var defaultPieces = List.of(
            ExNihiloItems.LEAD,
            ExNihiloItems.NICKEL,
            ExNihiloItems.SILVER,
            ExNihiloItems.TIN,
            ExNihiloItems.ALUMINUM,
            ExNihiloItems.PLATINUM,
            ExNihiloItems.URANIUM,
            ExNihiloItems.ZINC
        );

        for (var piece : defaultPieces) {
            var oreItem = Objects.requireNonNull(piece.getRawOreItem())
                .map(RegistryObject::get, item -> item);
            CompactingRecipeBuilder.builder(oreItem)
                .setInput(Ingredient.of(piece.getPieceItem()), 4)
                .build(consumer);
        }

        // novamachina.exnihilosequentia.common.ExNihiloRecipeGenerator#registerPebbleBlocks
        var pebbleMap = Map.ofEntries(
            Map.entry(Blocks.ANDESITE, ExNihiloItems.PEBBLE_ANDESITE.get()),
            Map.entry(Blocks.BASALT, ExNihiloItems.PEBBLE_BASALT.get()),
            Map.entry(Blocks.BLACKSTONE, ExNihiloItems.PEBBLE_BLACKSTONE.get()),
            Map.entry(Blocks.COBBLESTONE, ExNihiloItems.PEBBLE_STONE.get()),
            Map.entry(Blocks.CALCITE, ExNihiloItems.PEBBLE_CALCITE.get()),
            Map.entry(Blocks.DEEPSLATE, ExNihiloItems.PEBBLE_DEEPSLATE.get()),
            Map.entry(Blocks.DIORITE, ExNihiloItems.PEBBLE_DIORITE.get()),
            Map.entry(Blocks.DRIPSTONE_BLOCK, ExNihiloItems.PEBBLE_DRIPSTONE.get()),
            Map.entry(Blocks.END_STONE, ExNihiloItems.PEBBLE_END_STONE.get()),
            Map.entry(Blocks.GRANITE, ExNihiloItems.PEBBLE_GRANITE.get()),
            Map.entry(Blocks.NETHERRACK, ExNihiloItems.PEBBLE_NETHERRACK.get()),
            Map.entry(Blocks.TUFF, ExNihiloItems.PEBBLE_TUFF.get())
        );

        pebbleMap.forEach((block, pebble) -> CompactingRecipeBuilder.builder(block.asItem())
            .setInput(Ingredient.of(pebble), 4)
            .build(consumer));
    }
}
