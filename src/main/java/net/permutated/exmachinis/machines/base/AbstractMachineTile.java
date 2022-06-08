package net.permutated.exmachinis.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.ExNihiloAPI;
import net.permutated.exmachinis.util.OverlayItemHandler;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractMachineTile extends BlockEntity {
    protected AbstractMachineTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    protected WorkStatus workStatus = WorkStatus.NONE;

    protected final EnergyStorage energyStorage = new MachineEnergyStorage(100_000, 1_000);

    protected final ItemStackHandler itemStackHandler = new MachineItemStackHandler(9) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return AbstractMachineTile.this.isItemValid(stack);
        }
    };

    protected final ItemStackHandler upgradeStackHandler = new MachineItemStackHandler(enableMeshSlot() ? 2 : 1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() instanceof UpgradeItem;
            } else {
                return enableMeshSlot() && ExNihiloAPI.isMeshItem(stack);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 3 : 1;
        }
    };

    protected abstract boolean isItemValid(ItemStack stack);

    protected boolean enableMeshSlot() {
        return false;
    }

    protected final LazyOptional<EnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);
    protected final LazyOptional<IItemHandler> overlay = LazyOptional.of(() -> new OverlayItemHandler(itemStackHandler, upgradeStackHandler));

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return overlay.cast();
            } else {
                return handler.cast();
            }
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    public void dropItems() {
        AbstractMachineTile.dropItems(level, worldPosition, itemStackHandler);
        AbstractMachineTile.dropItems(level, worldPosition, upgradeStackHandler);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    int remainder = 0;
    private long lastTicked = 0L;

    public boolean canTick(final int every) {
        long gameTime = level != null ? level.getGameTime() : 0L;
        if (gameTime != lastTicked) {
            lastTicked = gameTime;
            remainder = (int) (offset(gameTime) % every);
            return remainder == 0;
        }
        return false;
    }

    int offset = 0;

    /**
     * Add a random offset between 0 and 19 ticks.
     * This is generated once per block entity on the first tick.
     *
     * @param gameTime the current game time
     * @return the tick delay with the saved offset
     */
    protected long offset(final long gameTime) {
        if (offset == 0) offset += ThreadLocalRandom.current().nextInt(0, 20);
        return gameTime + offset;
    }

    public abstract void tick();

    @SuppressWarnings("java:S1172") // unused arguments are required
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof AbstractMachineTile machineTile) {
            machineTile.tick();
        }
    }

    //TODO can this be merged with above code for gametime? gametime % max delay?
    protected AtomicInteger delayProgress = new AtomicInteger(0);
    public int getMaxWork() {
        return 60;
        //TODO config
    }

    public int getWork() {
        return remainder;
    }

    @Deprecated // remove TE access in frontend
    public void setWork(int delayProgress) {
        this.remainder = delayProgress;
    }

    public void setWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }
    public WorkStatus getWorkStatus() {
        return this.workStatus;
    }

    protected static void dropItems(@Nullable Level world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }

    /**
     * Serialize data to be sent to the GUI on the client.
     * <p>
     * Overrides MUST call the super method first to ensure correct deserialization.
     *
     * @param packetBuffer the packet ready to be filled
     */
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBoolean(enableMeshSlot());
        packetBuffer.writeBlockPos(worldPosition);
        packetBuffer.writeEnum(workStatus);
    }

    // Save TE data to disk
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put(Constants.NBT.ENERGY, energyStorage.serializeNBT());
        tag.put(Constants.NBT.INVENTORY, itemStackHandler.serializeNBT());
        tag.put(Constants.NBT.UPGRADES, upgradeStackHandler.serializeNBT());
        writeFields(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeFields(CompoundTag tag) {
        // no extra TE data
    }

    // Load TE data from disk
    @Override
    public void load(CompoundTag tag) {
        energyStorage.deserializeNBT(tag.getCompound(Constants.NBT.ENERGY));
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INVENTORY));
        upgradeStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.UPGRADES));
        readFields(tag);
        super.load(tag);
    }

    // Read TE data from a provided CompoundNBT
    private void readFields(@Nullable CompoundTag tag) {
        // no extra TE data
    }

    // Called whenever a client loads a new chunk
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        writeFields(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nullable CompoundTag tag) {
        readFields(tag);
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    // Handles the update packet received from the server
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.handleUpdateTag(pkt.getTag());
    }

    public class MachineItemStackHandler extends ItemStackHandler {
        public MachineItemStackHandler(int size) {
            super(size);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    }

    public class MachineEnergyStorage extends EnergyStorage {

        public MachineEnergyStorage(int capacity, int maxTransfer) {
            super(capacity, maxTransfer, 0);
        }

        public void onEnergyChanged() {
            setChanged();
        }
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int rc = super.receiveEnergy(maxReceive, simulate);
            if (rc > 0 && !simulate) {
                onEnergyChanged();
            }
            return rc;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int rc = super.extractEnergy(maxExtract, simulate);
            if (rc > 0 && !simulate) {
                onEnergyChanged();
            }
            return rc;
        }

        public void setEnergy(int energy) {
            this.energy = energy;
            onEnergyChanged();
        }

        public void addEnergy(int energy) {
            this.energy += energy;
            if (this.energy > getMaxEnergyStored()) {
                this.energy = getEnergyStored();
            }
            onEnergyChanged();
        }

        public void consumeEnergy(int energy) {
            this.energy -= energy;
            if (this.energy < 0) {
                this.energy = 0;
            }
            onEnergyChanged();
        }
    }
}
