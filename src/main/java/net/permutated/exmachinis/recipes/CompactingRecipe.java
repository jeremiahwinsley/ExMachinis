package net.permutated.exmachinis.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.SerializerUtil;

import javax.annotation.Nullable;

public class CompactingRecipe extends AbstractMachineRecipe {

    private final Ingredient input;
    private final ItemStack output;

    public CompactingRecipe(ResourceLocation id, Ingredient input, ItemStack output) {
        super(id);
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkNotNull(output, "output cannot be null.");

        this.input = input;
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
        input.toNetwork(buffer);
        buffer.writeItem(output);
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public static class Serializer extends AbstractSerializer<CompactingRecipe> {
        @Override
        public CompactingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient input = Ingredient.fromJson(jsonObject.getAsJsonObject(Constants.JSON.INPUT));
            ItemStack output = SerializerUtil.getItemStack(jsonObject, Constants.JSON.OUTPUT);

            return new CompactingRecipe(resourceLocation, input, output);
        }

        @Nullable
        @Override
        public CompactingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new CompactingRecipe(resourceLocation, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompactingRecipe compactingRecipe) {
            compactingRecipe.write(buffer);
        }
    }
}
