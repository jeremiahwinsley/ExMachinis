package net.permutated.exmachinis.machines.sieve;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.util.WorkStatus;

import java.util.List;

import static net.permutated.exmachinis.util.ItemStackUtil.multiplyStack;

public class FluxSieveTile extends AbstractMachineTile {
    public FluxSieveTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_SIEVE_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    protected boolean enableMeshSlot() {
        return true;
    }

    protected boolean isWaterlogged() {
        return this.getBlockState().getValue(FluxSieveBlock.WATERLOGGED);
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel && canTick(getUpgradeTickDelay())) {

            // ensure that block above is a valid inventory, and get an IItemHandler
            BlockPos above = getBlockPos().above();
            BlockEntity target = level.getBlockEntity(above);
            if (target == null) {
                workStatus = WorkStatus.MISSING_INVENTORY;
                return;
            }

            IItemHandler itemHandler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN)
                .resolve()
                .orElse(null);
            if (itemHandler == null) {
                workStatus = WorkStatus.MISSING_INVENTORY;
                return;
            } else {
                workStatus = WorkStatus.WORKING;
            }

            int cost = getUpgradeEnergyCost();
            int stored = energyStorage.getEnergyStored();
            int maxProcessed = getUpgradeItemsProcessed();

            if (stored < cost) {
                // not enough energy for an operation
                workStatus = WorkStatus.OUT_OF_ENERGY;
                return;
            } else if (cost > 0) { // don't need to run this if cost is 0
                // figure out how many operations we can do with the remaining energy
                int quotient = (stored / cost);
                if (quotient < maxProcessed) {
                    maxProcessed = quotient;
                }
            }

            var meshStack = upgradeStackHandler.getStackInSlot(1);
            if (!ExNihiloAPI.isMeshItem(meshStack)) {
                workStatus = WorkStatus.MISSING_MESH;
                return;
            }

            // iterate input slots until reaching the end, or running out of operations
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (maxProcessed == 0) {
                    return;
                }

                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canSieve(stack, meshStack, isWaterlogged())) {
                    int multiplier;

                    // shrink stack count by remaining operations or current stack size, whichever is smaller
                    var copy = stack.copy();
                    int count = stack.getCount();
                    if (count >= maxProcessed) {
                        multiplier = maxProcessed;
                        copy.shrink(maxProcessed);
                        maxProcessed = 0;
                    } else {
                        multiplier = count;
                        copy = ItemStack.EMPTY;
                        maxProcessed -= count;
                    }

                    // do not simulate inserts first
                    // since the drops are not determinate, this avoids an issue
                    // where the drops could be selected by pre-filling the output.

                    int totalCost = cost * multiplier;
                    boolean result = energyStorage.consumeEnergy(totalCost, true);
                    if (!result) {
                        // simulating energy use failed
                        workStatus = WorkStatus.OUT_OF_ENERGY;
                        return;
                    }

                    itemStackHandler.setStackInSlot(i, copy); // shrink input
                    energyStorage.consumeEnergy(totalCost, false);
                    processResults(itemHandler, meshStack, stack, multiplier, false);
                }
            }
        }
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"}) // kept for consistency
    private boolean processResults(IItemHandler itemHandler, ItemStack meshStack, ItemStack stack, int multiplier, boolean simulate) {
        // process sieve results
        ExNihiloAPI.getSieveResult(stack, meshStack, isWaterlogged()).stream()
            .map(result -> multiplyStack(result, multiplier))
            .flatMap(List::stream)
            .map(output -> ItemHandlerHelper.insertItemStacked(itemHandler, output, simulate))
            .forEach(response -> {
                if (!response.isEmpty()) {
                    workStatus = WorkStatus.INVENTORY_FULL;
                }
            });
        return workStatus == WorkStatus.WORKING;
    }
}
