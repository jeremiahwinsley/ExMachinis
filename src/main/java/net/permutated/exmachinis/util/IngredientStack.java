package net.permutated.exmachinis.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {
    public static final IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, 1);

    public boolean test(ItemStack itemStack) {
        return itemStack.getCount() >= count && ingredient.test(itemStack);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add(Constants.JSON.INGREDIENT, ingredient.toJson());
        json.addProperty(Constants.JSON.COUNT, count);
        return json;
    }

    public static IngredientStack fromJson(JsonObject jsonObject) {
        var ingredient = Ingredient.fromJson(jsonObject.get(Constants.JSON.INGREDIENT));
        int count = jsonObject.get(Constants.JSON.COUNT).getAsInt();
        return new IngredientStack(ingredient, count);
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
