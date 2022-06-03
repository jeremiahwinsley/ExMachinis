package net.permutated.exmachinis.machines.sieve;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;

public class FluxSieveTile extends AbstractMachineTile {
    public FluxSieveTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_SIEVE_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(60)) {
            // tick
        }
    }


}
