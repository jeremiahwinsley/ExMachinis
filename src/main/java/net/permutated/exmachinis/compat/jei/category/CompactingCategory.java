package net.permutated.exmachinis.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.ResourceUtil;
import net.permutated.exmachinis.util.TranslationKey;

import java.util.Arrays;
import java.util.List;

public class CompactingCategory implements IRecipeCategory<CompactingRecipe> {

    private final IDrawable icon;
    private final IDrawable background;
    private final TranslatableComponent title;

    public static final RecipeType<CompactingRecipe> RECIPE_TYPE = RecipeType.create(ExMachinis.MODID, Constants.COMPACTING, CompactingRecipe.class);

    public CompactingCategory(final IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModRegistry.FLUX_COMPACTOR_BLOCK.get()));
        background = guiHelper.createDrawable(mezz.jei.config.Constants.RECIPE_GUI_VANILLA, 0, 220, 82, 34);
        title = new TranslatableComponent(TranslationKey.jei(Constants.COMPACTING));
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends CompactingRecipe> getRecipeClass() {
        return getRecipeType().getRecipeClass();
    }

    @Override
    public RecipeType<CompactingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompactingRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> inputs = Arrays.stream(recipe.getIngredient().ingredient().getItems())
            .map(stack -> ItemHandlerHelper.copyStackWithSize(stack, recipe.getIngredient().count()))
            .toList();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
            .addIngredients(VanillaTypes.ITEM_STACK, inputs);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9)
            .addItemStack(recipe.getOutput());
    }
}
