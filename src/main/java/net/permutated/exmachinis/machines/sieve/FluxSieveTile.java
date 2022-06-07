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
import net.permutated.exmachinis.util.ExNihiloAPI;
import net.permutated.exmachinis.util.WorkStatus;

import java.util.List;

import static net.permutated.exmachinis.util.ItemStackUtil.multiplyStack;

public class FluxSieveTile extends AbstractMachineTile {
    public FluxSieveTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_SIEVE_TILE.get(), pos, state);
    }

    //TODO mesh inventory?

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    protected boolean isWaterlogged() {
        return this.getBlockState().getValue(FluxSieveBlock.WATERLOGGED);
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(60)) {

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

            int remaining = 8;

            var meshStack = ExNihiloAPI.getMeshItem();

            // iterate input slots until reaching the end, or running out of operations
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (remaining == 0) {
                    break;
                }

                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canSieve(stack, meshStack, isWaterlogged())) {
                    int multiplier;

                    // shrink stack count by remaining operations or current stack size, whichever is smaller
                    var copy = stack.copy();
                    int count = stack.getCount();
                    if (count >= remaining) {
                        multiplier = remaining;
                        copy.shrink(remaining);
                        remaining = 0;
                    } else {
                        multiplier = count;
                        copy = ItemStack.EMPTY;
                        remaining -= count;
                    }
                    itemStackHandler.setStackInSlot(i, copy);

                    // process hammer results
                    ExNihiloAPI.getSieveResult(stack, meshStack, isWaterlogged()).stream()
                        .map(result -> multiplyStack(result, multiplier))
                        .flatMap(List::stream)
                        .map(output -> ItemHandlerHelper.insertItemStacked(itemHandler, output, false))
                        .forEach(response -> {
                            if (!response.isEmpty()) {
                                workStatus = WorkStatus.INVENTORY_FULL;
                            }
                        });
                }
            }
        }
    }


}
