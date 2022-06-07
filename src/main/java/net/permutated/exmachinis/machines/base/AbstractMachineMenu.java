package net.permutated.exmachinis.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractMachineMenu extends AbstractContainerMenu {

    @Nullable // should only be accessed from server
    private final AbstractMachineTile tileEntity;

    protected boolean enableMeshSlot;
    protected int energyStored;
    protected final BlockPos blockPos;
    protected final WorkStatus workStatus;

    protected int totalSlots = 0;

    protected AbstractMachineMenu(@Nullable MenuType<?> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(containerType, windowId);

        enableMeshSlot = packetBuffer.readBoolean();
        energyStored = packetBuffer.readInt();
        blockPos = packetBuffer.readBlockPos();
        workStatus = packetBuffer.readEnum(WorkStatus.class);

        Level world = playerInventory.player.getCommandSenderWorld();

        tileEntity = (AbstractMachineTile) world.getBlockEntity(blockPos);
        IItemHandler wrappedInventory = new InvWrapper(playerInventory);

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                int index = 0;
                addSlot(new SlotItemHandler(handler, index++, 116, 53));
                if (enableMeshSlot) {
                    addSlot(new SlotItemHandler(handler, index++, 79, 35));
                }

                // 3 x 3
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        addSlot(new SlotItemHandler(handler, index++, 8 + j * 18, 18 + i * 18));
                    }
                }
            });
        }

        registerPlayerSlots(wrappedInventory);
    }

    protected abstract RegistryObject<Block> getBlock();

    protected WorkStatus getWorkStatus() {
        return workStatus;
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        if (tileEntity != null) {
            Level world = tileEntity.getLevel();
            if (world != null) {
                ContainerLevelAccess callable = ContainerLevelAccess.create(world, tileEntity.getBlockPos());
                return stillValid(callable, playerEntity, getBlock().get());
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int inventorySize = totalSlots;

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < inventorySize) {
                if (!this.moveItemStackTo(stack, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void registerPlayerSlots(IItemHandler wrappedInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(wrappedInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(wrappedInventory, i, 8 + i * 18, 142));
        }
    }
}
