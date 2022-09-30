package net.permutated.exmachinis.data.builders;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.data.RecipeException;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;
import net.permutated.exmachinis.util.SerializerUtil;

import java.util.Objects;
import java.util.function.Consumer;

public class CompactingRecipeBuilder extends AbstractRecipeBuilder {

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

    public void build(Consumer<FinishedRecipe> consumer) {
        String path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(output.getItem())).getPath();
        build(consumer, id(path));
    }

    @Override
    protected AbstractResult getResult(ResourceLocation id) {
        return new CompactingRecipeBuilder.Result(id);
    }

    public class Result extends AbstractResult {
        public Result(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            jsonObject.add(Constants.JSON.INPUT, ingredient.toJson());
            jsonObject.add(Constants.JSON.OUTPUT, SerializerUtil.serializeItemStack(output));
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRegistry.COMPACTING_RECIPE_SERIALIZER.get();
        }

    }
}
