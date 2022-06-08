package net.permutated.exmachinis.machines.compactor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;

public class FluxCompactorTile extends AbstractMachineTile {
    public FluxCompactorTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_COMPACTOR_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(getMaxWork())) {
            // tick
        }
    }


}
