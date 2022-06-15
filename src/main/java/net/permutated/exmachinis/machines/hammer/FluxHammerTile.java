package net.permutated.exmachinis.machines.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class FluxHammerTile extends AbstractMachineTile {
    public FluxHammerTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_HAMMER_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(getUpgradeTickDelay())) {

            // ensure that facing block is a valid inventory, and get an IItemHandler
            Direction facing = getBlockState().getValue(FluxHammerBlock.FACING);
            BlockPos facingPos = getBlockPos().relative(facing);
            BlockEntity target = level.getBlockEntity(facingPos);
            if (target == null) {
                workStatus = WorkStatus.MISSING_INVENTORY;
                return;
            }

            IItemHandler itemHandler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())
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

            // iterate input slots until reaching the end, or running out of operations
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (maxProcessed == 0) {
                    break;
                }

                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canHammer(stack)) {
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

                    if (!processResults(itemHandler, stack, multiplier, true)) {
                        // simulating inserts failed
                        return;
                    }

                    int totalCost = cost * multiplier;
                    boolean result = energyStorage.consumeEnergy(totalCost, true);
                    if (!result) {
                        // simulating energy use failed
                        workStatus = WorkStatus.OUT_OF_ENERGY;
                        return;
                    }

                    itemStackHandler.setStackInSlot(i, copy); // shrink input
                    energyStorage.consumeEnergy(totalCost, false);
                    processResults(itemHandler, stack, multiplier, false);
                }
            }

            // refill an empty slot from inventory above, if it exists
            pullFromAbove();
        }
    }

    private boolean processResults(IItemHandler itemHandler, ItemStack stack, int multiplier, boolean simulate) {
        // process hammer results
        ExNihiloAPI.getHammerResult(stack).stream()
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

    private void pullFromAbove() {
        // optionally connect to an inventory above to pull from
        IItemHandler inputItemHandler = null;
        if (level != null && getBlockState().getValue(FluxHammerBlock.HOPPER).equals(Boolean.TRUE)) {
            // ensure that block above is a valid inventory, and get an IItemHandler
            BlockPos above = getBlockPos().above();
            BlockEntity targetAbove = level.getBlockEntity(above);
            if (targetAbove != null) {
                inputItemHandler = targetAbove.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN)
                    .resolve()
                    .orElse(null);
            }
        }

        if (inputItemHandler != null) {
            // iterate input slots until reaching the end, or finding a valid stack
            for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                ItemStack stack = inputItemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canHammer(stack)) {
                    // extract up to one stack
                    var extractResult = inputItemHandler.extractItem(i, stack.getMaxStackSize(), true);
                    var insertResult = ItemHandlerHelper.insertItemStacked(itemStackHandler, extractResult, true);

                    // only move items if the leftover amount is less than the extracted amount
                    // should always be true unless item.maxStackSize > slot.getMaxStackSize,
                    // or the internal inventory is full
                    if (extractResult.getCount() > insertResult.getCount()) {
                        int inserted = extractResult.getCount() - insertResult.getCount();
                        ItemStack extracted = inputItemHandler.extractItem(i, inserted, false);
                        ItemHandlerHelper.insertItemStacked(itemStackHandler, extracted, false);
                        return;
                    }
                }
            }
        }
    }
}
