package com.leo.voidminers.menu;

import com.leo.voidminers.block.entity.SolarControllerBE;
import com.leo.voidminers.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;

public class SolarControllerMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos pos;
    private SolarControllerBE be;

    // Data indices
    private static final int IDX_ENERGY = 0;
    private static final int IDX_MAX_ENERGY = 1;
    private static final int IDX_RFT = 2;
    private static final int IDX_SUN = 3; // percent 0..100
    private static final int IDX_ACTIVE = 4; // 0/1
    private static final int IDX_WORKING = 5; // 0/1

    private final ContainerData data;

    // Server-side constructor
    public SolarControllerMenu(int id, Inventory playerInv, SolarControllerBE be) {
        super(ModMenuTypes.SOLAR_CONTROLLER_MENU.get(), id);
        this.level = playerInv.player.level();
        this.pos = be.getBlockPos();
        this.be = be;
        this.data = new SimpleContainerData(6) {
            @Override
            public int get(int index) {
                if (SolarControllerMenu.this.be == null) return 0;
                return switch (index) {
                    case IDX_ENERGY -> SolarControllerMenu.this.be.getEnergyStored();
                    case IDX_MAX_ENERGY -> SolarControllerMenu.this.be.getMaxEnergyStored();
                    case IDX_RFT -> SolarControllerMenu.this.be.getRfTick();
                    case IDX_SUN -> Math.round(SolarControllerMenu.this.be.getSolarMultiplier() * 100f);
                    case IDX_ACTIVE -> SolarControllerMenu.this.be.isActive() ? 1 : 0;
                    case IDX_WORKING -> SolarControllerMenu.this.be.isWorking() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() { return 6; }
        };

        addDataSlots(this.data);
    }

    // Client-side constructor (reads pos)
    public SolarControllerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        super(ModMenuTypes.SOLAR_CONTROLLER_MENU.get(), id);
        this.level = playerInv.player.level();
        this.pos = buf.readBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (blockEntity instanceof SolarControllerBE scbe) {
            this.be = scbe;
        }
        this.data = new SimpleContainerData(6);
        addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level.getBlockEntity(this.pos) instanceof SolarControllerBE &&
            player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
            ) <= 64.0;
    }

    // Expose read-only values for the screen
    public int energyStored() { return data.get(IDX_ENERGY); }
    public int maxEnergy() { return Math.max(1, data.get(IDX_MAX_ENERGY)); }
    public int rfPerTick() { return data.get(IDX_RFT); }
    public int sunPercent() { return Math.min(100, Math.max(0, data.get(IDX_SUN))); }
    public boolean isActive() { return data.get(IDX_ACTIVE) != 0; }
    public boolean isWorking() { return data.get(IDX_WORKING) != 0; }
    public BlockPos getPos() { return pos; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No inventory slots in this menu
        return ItemStack.EMPTY;
    }
}
