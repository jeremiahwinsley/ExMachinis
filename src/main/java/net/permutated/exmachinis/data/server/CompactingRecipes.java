package net.permutated.exmachinis.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.data.builders.CompactingRecipeBuilder;
import novamachina.exnihilosequentia.common.init.ExNihiloItems;

import java.util.List;
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
    }
}
