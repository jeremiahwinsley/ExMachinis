package net.permutated.exmachinis.util;

public record TextureHolder(int progressOffsetX, int progressOffsetY,
                            int textureOffsetX, int textureOffsetY,
                            int textureWidth, int textureHeight) {

    public int getHeightFraction(float fraction) {
        return (int) (textureHeight * fraction);
    }

    public int progressHeightOffset(float fraction) {
        return progressOffsetY + (textureHeight - (int) (textureHeight * fraction));
    }
}
