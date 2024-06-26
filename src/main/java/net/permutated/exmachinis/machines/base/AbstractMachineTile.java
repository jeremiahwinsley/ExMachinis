package net.permutated.exmachinis.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.permutated.exmachinis.ConfigHolder;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.OverlayItemHandler;
import net.permutated.exmachinis.util.PipeItemHandler;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMachineTile extends BlockEntity {
    protected int version = 1;
    protected AbstractMachineTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    protected WorkStatus workStatus = WorkStatus.NONE;

    protected final MachineEnergyStorage energyStorage = new MachineEnergyStorage(getMaxEnergyStorage(), getMaxEnergyTransfer());

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

    protected int getMaxEnergyStorage() {
        return ConfigHolder.SERVER.energyBufferSize.get();
    }

    protected int getMaxEnergyTransfer() {
        return ConfigHolder.SERVER.maxEnergyPerTick.get();
    }

    protected int getUpgradeItemsProcessed() {
        var upgradeStack = upgradeStackHandler.getStackInSlot(0);
        if (upgradeStack.getItem() instanceof UpgradeItem upgradeItem && upgradeStack.getCount() > 0) {
            int upgradeCount = Mth.clamp(upgradeStack.getCount(), 1, 3);
            return upgradeItem.getTier().getItemsProcessed(upgradeCount);
        }
        return 1;
    }

    protected int getUpgradeTickDelay() {
        var upgradeStack = upgradeStackHandler.getStackInSlot(0);
        if (upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
            return switch (upgradeItem.getTier()) {
                case GOLD -> ConfigHolder.SERVER.goldTicksPerOperation.get();
                case DIAMOND -> ConfigHolder.SERVER.diamondTicksPerOperation.get();
                case NETHERITE -> ConfigHolder.SERVER.netheriteTicksPerOperation.get();
            };
        }
        return ConfigHolder.SERVER.goldTicksPerOperation.get();
    }

    protected int getUpgradeEnergyCost() {
        var upgradeStack = upgradeStackHandler.getStackInSlot(0);
        if (upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
            return switch (upgradeItem.getTier()) {
                case GOLD -> ConfigHolder.SERVER.goldEnergyPerBlock.get();
                case DIAMOND -> ConfigHolder.SERVER.diamondEnergyPerBlock.get();
                case NETHERITE -> ConfigHolder.SERVER.netheriteEnergyPerBlock.get();
            };
        }
        return ConfigHolder.SERVER.goldEnergyPerBlock.get();
    }

    protected abstract boolean isItemValid(ItemStack stack);

    protected boolean enableMeshSlot() {
        return false;
    }

    protected final IItemHandler handler = new PipeItemHandler(itemStackHandler);
    protected final IItemHandler overlay = new OverlayItemHandler(itemStackHandler, upgradeStackHandler);

    /**
     * Helper method for registering capabilities. Called by ForgeEventHandler.
     * @param event the registration event
     * @param blockEntityType the block entity being registered
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<? extends AbstractMachineTile> blockEntityType) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, blockEntityType, (machine, context) -> machine.energyStorage );
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType, (machine, side) -> {
            if (side == null) {
                return machine.overlay;
            } else {
                return machine.handler;
            }
        });
    }

    protected BlockCapabilityCache<IItemHandler, Direction> inputCache;
    protected BlockCapabilityCache<IItemHandler, Direction> outputCache;

    /**
     * Load an item capability for a given position and update cache if necessary.
     * @param serverLevel the current level
     * @param target the position to search
     * @param side the side to extract from
     * @return an item handler, if found
     */
    protected @Nullable IItemHandler findCapabilityForInput(ServerLevel serverLevel, BlockPos target, Direction side) {
        if (inputCache == null || !side.equals(inputCache.context())) {
            inputCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, target, side);
        }
        return inputCache.getCapability();
    }

    /**
     * Load an item capability for a given position and update cache if necessary.
     * @param serverLevel the current level
     * @param target the position to search
     * @param side the side to insert to
     * @return an item handler, if found
     */
    protected @Nullable IItemHandler findCapabilityForOutput(ServerLevel serverLevel, BlockPos target, Direction side) {
        if (outputCache == null || !side.equals(outputCache.context())) {
            outputCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, target, side);
        }
        return outputCache.getCapability();
    }


    public void dropItems() {
        AbstractMachineTile.dropItems(level, worldPosition, itemStackHandler);
        AbstractMachineTile.dropItems(level, worldPosition, upgradeStackHandler);
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

    public int getMaxWork() {
        return getUpgradeTickDelay();
    }

    public int getWork() {
        return remainder;
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
    }

    // Save TE data to disk
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt(Constants.NBT.VERSION, version);
        tag.put(Constants.NBT.ENERGY, energyStorage.serializeNBT());
        tag.put(Constants.NBT.INVENTORY, itemStackHandler.serializeNBT());
        tag.put(Constants.NBT.UPGRADES, upgradeStackHandler.serializeNBT());
    }


    // Load TE data from disk
    @Override
    public void load(CompoundTag tag) {
        version = tag.getInt(Constants.NBT.VERSION);
        energyStorage.deserializeNBT(tag.get(Constants.NBT.ENERGY));
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INVENTORY));
        upgradeStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.UPGRADES));
        super.load(tag);
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
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

        public MachineEnergyStorage(int capacity, int maxRecieve) {
            super(capacity, maxRecieve, 0);
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

        public boolean consumeEnergy(int request, boolean simulate) {
            int consumed = Math.max(0, request);
            if (this.energy > consumed) {
                if (!simulate) {
                    this.energy -= consumed;
                    onEnergyChanged();
                }
                return true;
            }
            return false;
        }
    }
}
