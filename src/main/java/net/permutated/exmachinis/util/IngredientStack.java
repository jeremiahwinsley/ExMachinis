package net.permutated.exmachinis.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {
    public static final IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, 1);
    public static final Codec<IngredientStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf(Constants.JSON.INGREDIENT).forGetter(IngredientStack::ingredient),
        Codec.INT.fieldOf(Constants.JSON.COUNT).forGetter(IngredientStack::count)
    ).apply(instance, IngredientStack::new));

    public boolean test(ItemStack itemStack) {
        return itemStack.getCount() >= count && ingredient.test(itemStack);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        buf.writeInt(count);
    }

    public static IngredientStack fromNetwork(FriendlyByteBuf buf) {
        var ingredient = Ingredient.fromNetwork(buf);
        int count = buf.readInt();
        return new IngredientStack(ingredient, count);
    }
}
