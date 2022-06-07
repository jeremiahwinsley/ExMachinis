package net.permutated.exmachinis.data.builders;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.permutated.exmachinis.ExMachinis;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class AbstractRecipeBuilder {

    protected abstract String getPrefix();

    protected ResourceLocation id(String path) {
        return new ResourceLocation(ExMachinis.MODID, getPrefix() + "/" + path);
    }

    protected abstract void validate(ResourceLocation id);

    protected abstract AbstractResult getResult(ResourceLocation id);

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        validate(id);
        consumer.accept(getResult(id));
    }

    protected abstract static class AbstractResult implements FinishedRecipe {
        private final ResourceLocation id;

        protected AbstractResult(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }


        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
