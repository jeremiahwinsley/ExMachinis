package net.permutated.exmachinis.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompactingRegistry {

    private final Object lock = new Object();
    private List<CompactingRecipe> recipeList = Collections.emptyList();
    private final Map<Item, CompactingRecipe> recipeByItemCache = new ConcurrentHashMap<>();

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
        synchronized (lock) {
            recipeList = List.copyOf(recipes);
            recipeByItemCache.clear();
        }
    }

    public List<CompactingRecipe> getRecipeList() {
        synchronized (lock) {
            return recipeList;
        }
    }

    public void clearRecipes() {
        synchronized (lock) {
            recipeList = Collections.emptyList();
            recipeByItemCache.clear();
        }
    }
}
