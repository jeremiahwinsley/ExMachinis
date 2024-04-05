package net.permutated.exmachinis.data.builders;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.recipes.AbstractMachineRecipe;

public abstract class AbstractRecipeBuilder<T extends AbstractMachineRecipe> {

    protected abstract String getPrefix();

    protected ResourceLocation id(String path) {
        return new ResourceLocation(ExMachinis.MODID, getPrefix() + "/" + path);
    }

    protected abstract void validate(ResourceLocation id);

    protected abstract T getResult();

    public void build(RecipeOutput consumer, ResourceLocation id) {
        validate(id);
        consumer.accept(id, getResult(), null);
    }
}
