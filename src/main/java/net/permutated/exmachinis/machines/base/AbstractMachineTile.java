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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMachineTile extends BlockEntity {
    protected AbstractMachineTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    protected WorkStatus workStatus = WorkStatus.NONE;

    public static final int SLOTS = 9;

    protected final ItemStackHandler itemStackHandler = new MachineItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return AbstractMachineTile.this.isItemValid(stack);
        }
    };

    protected final ItemStackHandler upgradeInventory = new MachineItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof UpgradeItem;
        }
    };

    protected abstract boolean isItemValid(ItemStack stack);

    protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public void dropItems() {
        AbstractMachineTile.dropItems(level, worldPosition, itemStackHandler);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    private long lastTicked = 0L;

    public boolean canTick(final int every) {
        long gameTime = level != null ? level.getGameTime() : 0L;
        if (offset(gameTime) % every == 0 && gameTime != lastTicked) {
            lastTicked = gameTime;
            return true;
        } else {
            return false;
        }
    }

    int offset = 0;
    /**
     * Add a random offset between 0 and 19 ticks.
     * This is generated once per block entity on the first tick.
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
        packetBuffer.writeBlockPos(worldPosition);
        packetBuffer.writeEnum(workStatus);
    }

    // Save TE data to disk
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put(Constants.NBT.INV, itemStackHandler.serializeNBT());
        writeFields(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeFields(CompoundTag tag) {
        // no extra TE data
    }

    // Load TE data from disk
    @Override
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INV));
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

}
