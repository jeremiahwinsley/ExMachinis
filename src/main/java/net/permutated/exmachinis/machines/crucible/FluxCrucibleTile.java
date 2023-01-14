package net.permutated.exmachinis.machines.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.permutated.exmachinis.ConfigHolder;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.FluidStackUtil;
import net.permutated.exmachinis.util.FluidTankWrapper;
import net.permutated.exmachinis.util.WorkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluxCrucibleTile extends AbstractMachineTile {
    public FluxCrucibleTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_CRUCIBLE_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    private FluidStack lastSync = FluidStack.EMPTY;
    protected final FluidTank fluidTank = new FluidTank(128 * FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    protected final LazyOptional<FluidTankWrapper> tankHolder = LazyOptional.of(() -> new FluidTankWrapper(fluidTank));
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tankHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    public FluidStack getFluidStack() {
        return fluidTank.getFluid().copy();
    }

    public int getFluidCapacity() {
        return fluidTank.getCapacity();
    }


    public static class CrucibleTierConfig extends TierConfig {
        public static final TierConfig INSTANCE = new CrucibleTierConfig();
        @Override
        protected UpgradeItem.Tier getMaxUpgradeTier() {
            return ConfigHolder.SERVER.crucibleMaxUpgradeTier.get();
        }
    }

    @Override
    protected TierConfig tierConfig() {
        return CrucibleTierConfig.INSTANCE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put(Constants.NBT.FLUID, fluidTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        fluidTank.readFromNBT(tag.getCompound(Constants.NBT.FLUID));
        super.load(tag);
    }

    @Override
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeInt(getFluidCapacity());
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            handleUpdateTag(pkt.getTag());
        }
    }

    // Tag to be sent to the client
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(Constants.NBT.FLUID, fluidTank.writeToNBT(new CompoundTag()));
        return tag;
    }

    // Handle tag received on the client
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains(Constants.NBT.FLUID)) {
            fluidTank.readFromNBT(tag.getCompound(Constants.NBT.FLUID));
        } else {
            fluidTank.setFluid(FluidStack.EMPTY);
        }
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(getUpgradeTickDelay())) {
            Boolean enabled = getBlockState().getValue(AbstractMachineBlock.ENABLED);
            if (Boolean.FALSE.equals(enabled)) {
                workStatus = WorkStatus.REDSTONE_DISABLED;
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

                // TODO validate output fluid amount?
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (ExNihiloAPI.getCrucibleResult(stack).isPresent()) {
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

                    if (!processResults(fluidTank, stack, multiplier, true)) {
                        // simulating inserts failed
                        break;
                    }

                    int totalCost = cost * multiplier;
                    boolean result = energyStorage.consumeEnergy(totalCost, true);
                    if (!result) {
                        // simulating energy use failed
                        workStatus = WorkStatus.OUT_OF_ENERGY;
                        break;
                    }

                    itemStackHandler.setStackInSlot(i, copy); // shrink input
                    energyStorage.consumeEnergy(totalCost, false);
                    processResults(fluidTank, stack, multiplier, false);
                }
            }
            dumpBuffer();
            if (!fluidTank.getFluid().isFluidStackIdentical(lastSync)) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                lastSync = fluidTank.getFluid().copy();
            }
        }
    }

    private boolean processResults(IFluidHandler fluidHandler, ItemStack stack, int multiplier, boolean simulate) {
        // process crucible results
        ExNihiloAPI.getCrucibleResult(stack)
            .map(result -> FluidStackUtil.multiplyStackCount(result, multiplier))
            .map(output -> FluidStackUtil.insertFluid(fluidHandler, output, simulate))
            .ifPresent(response -> {
                if (!response.isEmpty()) {
                    workStatus = WorkStatus.INVENTORY_FULL;
                }
            });
        return workStatus == WorkStatus.WORKING;
    }

    /**
     * Attempt to dump the internal fluid tank to the output.
     */
    private void dumpBuffer() {
        if (level instanceof ServerLevel serverLevel) {
            // ensure that the output is a valid inventory, and get an IFluidHandler
            Direction output = getBlockState().getValue(AbstractMachineBlock.OUTPUT);
            BlockPos outPos = getBlockPos().relative(output);
            BlockEntity target = serverLevel.getBlockEntity(outPos);
            if (target == null) {
                return;
            }

            IFluidHandler fluidHandler = target.getCapability(ForgeCapabilities.FLUID_HANDLER, output.getOpposite())
                .resolve()
                .orElse(null);

            if (fluidHandler == null) {
                return;
            }

            FluidStack buffered = fluidTank.getFluid().copy();
            FluidStack result = FluidStackUtil.insertFluid(fluidHandler, buffered, true);
            if (result.getAmount() < buffered.getAmount()) {
                FluidStack execute = FluidStackUtil.insertFluid(fluidHandler, buffered, false);
                fluidTank.setFluid(execute);
            }
        }
    }
}
