package net.permutated.exmachinis.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;
import net.permutated.exmachinis.util.SerializerUtil;

import javax.annotation.Nullable;

public class CompactingRecipe extends AbstractMachineRecipe {
    public static final CompactingRecipe EMPTY = new CompactingRecipe(new ResourceLocation("empty"), IngredientStack.EMPTY, ItemStack.EMPTY);
    private final IngredientStack ingredient;
    private final ItemStack output;

    public CompactingRecipe(ResourceLocation id, IngredientStack input, ItemStack output) {
        super(id);
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkState(input.count() > 0, "input count must be greater than 0");
        Preconditions.checkNotNull(output, "output cannot be null.");

        this.ingredient = input;
        this.output = output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.COMPACTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRegistry.COMPACTING_RECIPE_TYPE.get();
    }

    public void write(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeItem(output);
    }

    public IngredientStack getIngredient() {
        return ingredient;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public static class Serializer extends AbstractSerializer<CompactingRecipe> {
        @Override
        public CompactingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            IngredientStack input = IngredientStack.fromJson(jsonObject.getAsJsonObject(Constants.JSON.INPUT));
            ItemStack output = SerializerUtil.getItemStack(jsonObject, Constants.JSON.OUTPUT);

            return new CompactingRecipe(resourceLocation, input, output);
        }

        @Nullable
        @Override
        public CompactingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            IngredientStack input = IngredientStack.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new CompactingRecipe(resourceLocation, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompactingRecipe compactingRecipe) {
            compactingRecipe.write(buffer);
        }
    }
}
