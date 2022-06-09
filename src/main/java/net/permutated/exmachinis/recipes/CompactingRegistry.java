package net.permutated.exmachinis.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompactingRegistry {

    private static List<CompactingRecipe> recipeList = Collections.emptyList();
    private static final Map<Item, CompactingRecipe> recipeByItemCache = new ConcurrentHashMap<>();

    public CompactingRecipe findRecipe(final Item item) {
        return recipeByItemCache.computeIfAbsent(item, it -> {
            final ItemStack itemStack = new ItemStack(it);
            return recipeList.stream()
                .filter(recipe -> recipe.getIngredient().ingredient().test(itemStack))
                .findFirst()
                .orElse(CompactingRecipe.EMPTY);
        });
    }

    public void setRecipeList(final List<CompactingRecipe> recipes) {
        synchronized (this) {
            recipeList = List.copyOf(recipes);
            recipeByItemCache.clear();
        }
    }

    public List<CompactingRecipe> getRecipeList() {
        synchronized (this) {
            return recipeList;
        }
    }

    public void clearRecipes() {
        synchronized (this) {
            recipeList = Collections.emptyList();
            recipeByItemCache.clear();
        }
    }
}
