package net.permutated.exmachinis.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;

public class CompactingRecipe extends AbstractMachineRecipe {
    public static final CompactingRecipe EMPTY = new CompactingRecipe(IngredientStack.EMPTY, ItemStack.EMPTY);
    private final IngredientStack ingredient;
    private final ItemStack output;

    public CompactingRecipe(IngredientStack input, ItemStack output) {
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

    public static class Serializer implements RecipeSerializer<CompactingRecipe> {
        @Override
        public Codec<CompactingRecipe> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                IngredientStack.CODEC.fieldOf(Constants.JSON.INPUT).forGetter(CompactingRecipe::getIngredient),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf(Constants.JSON.OUTPUT).forGetter(CompactingRecipe::getOutput)
            ).apply(instance, CompactingRecipe::new));
        }

        @Override
        public CompactingRecipe fromNetwork(FriendlyByteBuf buffer) {
            IngredientStack input = IngredientStack.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new CompactingRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompactingRecipe compactingRecipe) {
            compactingRecipe.write(buffer);
        }
    }
}
