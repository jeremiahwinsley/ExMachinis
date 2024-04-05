package net.permutated.exmachinis.data.builders;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.permutated.exmachinis.data.RecipeException;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;

import java.util.Objects;

public class CompactingRecipeBuilder extends AbstractRecipeBuilder<CompactingRecipe> {

    private IngredientStack ingredient = new IngredientStack(Ingredient.EMPTY, 0);
    private final ItemStack output;

    @Override
    protected String getPrefix() {
        return Constants.COMPACTING;
    }

    public CompactingRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static CompactingRecipeBuilder builder(Item output, int count) {
        return new CompactingRecipeBuilder(new ItemStack(output, count));
    }

    public static CompactingRecipeBuilder builder(Item output) {
        return new CompactingRecipeBuilder(new ItemStack(output));
    }

    public CompactingRecipeBuilder setInput(Ingredient input, int count) {
        this.ingredient = new IngredientStack(input, count);
        return this;
    }

    public CompactingRecipeBuilder setInput(ItemLike input, int count) {
        return setInput(Ingredient.of(input), count);
    }

    public CompactingRecipeBuilder setInput(TagKey<Item> input, int count) {
        return setInput(Ingredient.of(input), count);
    }

    protected void validate(ResourceLocation id) {
        if (Ingredient.EMPTY.equals(ingredient.ingredient())) {
            throw new RecipeException(id.toString(), "input is required");
        }
    }

    public void build(RecipeOutput consumer) {
        String path = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(output.getItem())).getPath();
        build(consumer, id(path));
    }

    @Override
    protected CompactingRecipe getResult() {
        return new CompactingRecipe(ingredient, output);
    }
}
