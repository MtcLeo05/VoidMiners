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

    // Data indices (use 16-bit pairs to avoid vanilla short truncation)
    private static final int IDX_ENERGY_LO = 0;
    private static final int IDX_ENERGY_HI = 1;
    private static final int IDX_MAX_ENERGY_LO = 2;
    private static final int IDX_MAX_ENERGY_HI = 3;
    private static final int IDX_RFT_LO = 4;
    private static final int IDX_RFT_HI = 5;
    private static final int IDX_SUN = 6;     // percent 0..100
    private static final int IDX_ACTIVE = 7;  // 0/1
    private static final int IDX_WORKING = 8; // 0/1
    private static final int IDX_BUFFER_FULL = 9; // 0/1

    private final ContainerData data;

    // Server-side constructor
    public SolarControllerMenu(int id, Inventory playerInv, SolarControllerBE be) {
        super(ModMenuTypes.SOLAR_CONTROLLER_MENU.get(), id);
        this.level = playerInv.player.level();
        this.pos = be.getBlockPos();
        this.be = be;
        this.data = new SimpleContainerData(10) {
            @Override
            public int get(int index) {
                if (SolarControllerMenu.this.be == null) return 0;
                return switch (index) {
                    case IDX_ENERGY_LO -> lo16(SolarControllerMenu.this.be.getEnergyStored());
                    case IDX_ENERGY_HI -> hi16(SolarControllerMenu.this.be.getEnergyStored());
                    case IDX_MAX_ENERGY_LO -> lo16(SolarControllerMenu.this.be.getMaxEnergyStored());
                    case IDX_MAX_ENERGY_HI -> hi16(SolarControllerMenu.this.be.getMaxEnergyStored());
                    case IDX_RFT_LO -> lo16(SolarControllerMenu.this.be.getRfTick());
                    case IDX_RFT_HI -> hi16(SolarControllerMenu.this.be.getRfTick());
                    case IDX_SUN -> Math.round(SolarControllerMenu.this.be.getSolarMultiplier() * 100f);
                    case IDX_ACTIVE -> SolarControllerMenu.this.be.isActive() ? 1 : 0;
                    case IDX_WORKING -> SolarControllerMenu.this.be.isWorking() ? 1 : 0;
                    case IDX_BUFFER_FULL -> (SolarControllerMenu.this.be.bufferFull ? 1 : 0);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() { return 10; }
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
        this.data = new SimpleContainerData(10);
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
    public int energyStored() { return from16(data.get(IDX_ENERGY_LO), data.get(IDX_ENERGY_HI)); }
    public int maxEnergy() { return Math.max(1, from16(data.get(IDX_MAX_ENERGY_LO), data.get(IDX_MAX_ENERGY_HI))); }
    public int rfPerTick() { return from16(data.get(IDX_RFT_LO), data.get(IDX_RFT_HI)); }
    public int sunPercent() { return Math.min(100, Math.max(0, data.get(IDX_SUN))); }
    public boolean isActive() { return data.get(IDX_ACTIVE) != 0; }
    public boolean isWorking() { return data.get(IDX_WORKING) != 0; }
    public boolean isBufferFull() { return data.get(IDX_BUFFER_FULL) != 0; }
    public BlockPos getPos() { return pos; }

    private static int lo16(int v) { return v & 0xFFFF; }
    private static int hi16(int v) { return (v >>> 16) & 0xFFFF; }
    private static int from16(int lo, int hi) { return (lo & 0xFFFF) | ((hi & 0xFFFF) << 16); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No inventory slots in this menu
        return ItemStack.EMPTY;
    }
}
