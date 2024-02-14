package net.permutated.exmachinis.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.jei.category.CompactingCategory;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.ResourceUtil;
import thedarkcolour.exdeorum.compat.jei.ExDeorumJeiPlugin;

import java.lang.reflect.Field;

import static net.permutated.exmachinis.util.TranslationKey.translateJei;


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

        try {
            // can't AT another mod, so hacks it is
            Field hammer = ExDeorumJeiPlugin.class.getDeclaredField("HAMMER");
            hammer.setAccessible(true);
            registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_HAMMER_BLOCK.get()), (RecipeType<?>) hammer.get(null));

            Field sieve = ExDeorumJeiPlugin.class.getDeclaredField("SIEVE");
            sieve.setAccessible(true);
            registration.addRecipeCatalyst(new ItemStack(ModRegistry.FLUX_SIEVE_BLOCK.get()), (RecipeType<?>) sieve.get(null));
        } catch (ReflectiveOperationException e) {
            // do nothing
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.<CompactingRecipe>addRecipes(CompactingCategory.RECIPE_TYPE, ModRegistry.COMPACTING_REGISTRY.getRecipeList());

        registration.addIngredientInfo(new ItemStack(ModRegistry.FLUX_COMPACTOR_BLOCK.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.FLUX_COMPACTOR));
        registration.addIngredientInfo(new ItemStack(ModRegistry.FLUX_HAMMER_BLOCK.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.FLUX_HAMMER));
        registration.addIngredientInfo(new ItemStack(ModRegistry.FLUX_SIEVE_BLOCK.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.FLUX_SIEVE));
    }
}
