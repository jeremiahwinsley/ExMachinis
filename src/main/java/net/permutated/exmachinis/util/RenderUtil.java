package net.permutated.exmachinis.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class RenderUtil {
    private RenderUtil() {
        // nothing to do
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack) {
        ResourceLocation texture = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture();

        return Minecraft.getInstance()
            .getModelManager()
            .getAtlas(InventoryMenu.BLOCK_ATLAS)
            .getSprite(texture);
    }

    public static int getFluidColor(FluidStack fluidStack) {
        return IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
    }

    public static void setShaderColorFromInt(int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }
}
