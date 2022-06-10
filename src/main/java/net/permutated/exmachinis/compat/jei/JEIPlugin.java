package net.permutated.exmachinis.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.jei.category.CompactingCategory;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.ResourceUtil;
import novamachina.exnihilosequentia.common.compat.jei.RecipeTypes;


@JeiPlugin
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {

    public ResourceLocation getPluginUid() {
        return ResourceUtil.prefix("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CompactingCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_COMPACTOR_BLOCK.get()), CompactingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_HAMMER_BLOCK.get()), RecipeTypes.HAMMER);
        registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_SIEVE_BLOCK.get()), RecipeTypes.DRY_SIEVE);
        registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_SIEVE_BLOCK.get()), RecipeTypes.WET_SIEVE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.<CompactingRecipe>addRecipes(CompactingCategory.RECIPE_TYPE, ModRegistry.COMPACTING_REGISTRY.getRecipeList());
    }
}
