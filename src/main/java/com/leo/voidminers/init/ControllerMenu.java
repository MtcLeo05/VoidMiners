package com.leo.voidminers.menu;

import com.leo.voidminers.block.entity.ControllerBaseBE;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ControllerMenu extends AbstractContainerMenu {
    public final ControllerBaseBE blockEntity;
    private final ContainerLevelAccess levelAccess;


    public ControllerMenu(int pContainerId, Inventory inv, ControllerBaseBE entity) {
        super(ModMenuTypes.CONTROLLER_MENU.get(), pContainerId);
        this.blockEntity = entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
   
    }


    public ControllerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (ControllerBaseBE) inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (pIndex < 36) {
                if (!this.moveItemStackTo(stackInSlot, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.levelAccess, pPlayer, this.blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}