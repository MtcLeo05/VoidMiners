package com.leo.voidminers.block.entity;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.*;

public class SolarControllerBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = 1000000;

    private ModEnergyStorage energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, 0, ENERGY_CAPACITY, 0);

    // Keep an item handler to mirror behavior (e.g., drop contents if any upgrades are placed in future)
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            SolarControllerBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public boolean foundStructure = false;
    public boolean showStructure = false;
    public boolean active;
    public boolean working;

    private final Map<BlockInWorld, ConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    private LazyOptional<ModEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public SolarControllerBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SOLAR_CONTROLLER_BE.get(), pPos, pBlockState);
    }

    public void setup(ResourceLocation structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        if (lazyEnergyHandler != null) {
            lazyEnergyHandler.invalidate();
        }

        int storage = ConfigLoader.getInstance().getSolarConfig(name).energyStorage();
        if (!ConfigLoader.getInstance().ALLOW_NO_ENERGY_MINERS && storage <= 0) storage = ENERGY_CAPACITY;

        // For generators: allow extraction, not receiving from outside
        energyHandler = new ModEnergyStorage(storage, 0, storage, energyHandler.getEnergyStored());
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    public int getBeamColor() {
        // Map color by base tier name (strip _solar suffix if present)
        String key = structure != null ? structure.getPath() : name;
        if (key == null) key = "";
        if (key.contains("_")) key = key.substring(0, key.indexOf('_'));
        return MiscUtil.colorMap.getOrDefault(key, 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> toRet = new ArrayList<>();

        if(working) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar.working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.energy", getRfTick())
            );
        }

        if (active) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar.not_working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.energy", getRfTick())
            );
        }

        if (foundStructure) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar.not_active")
            );
        }

        toRet.add(Component.translatable("tooltip." + VoidMiners.MODID + ".solar.missing_structure") );

        if (structure != null) {
            MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
                toRet.add(Component.literal(string + ": " + integer));
            });
        }

        return toRet;
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT());
        data.put("items", itemHandler.serializeNBT());
        if (name != null) data.putString("name", this.name);
        data.putBoolean("active", active);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        pTag.put(VoidMiners.MODID, data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(VoidMiners.MODID);
        if (data.isEmpty())
            return;

        if (data.contains("energy")) {
            energyHandler.deserializeNBT(data.get("energy"));
        }

        if (data.contains("items")) {
            itemHandler.deserializeNBT(data.getCompound("items"));
        }

        if (data.contains("name")) {
            name = data.getString("name");
        }

        if (data.contains("active")) {
            active = data.getBoolean("active");
        }

        if (data.contains("structure")) {
            structure = ResourceLocation.parse(data.getString("structure"));
        }

        if (data.contains("showStructure")) {
            showStructure = data.getBoolean("showStructure");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setupEnergyStorage();
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        if(getStructure() == null) {
            setup(structure, name);
        }

        checkStructure(pLevel, pPos);

        float sun = getSolarMultiplier();
        active = foundStructure && sun > 0.001f;
        level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);

        if(!active) return;

        working = energyHandler.getEnergyStored() < energyHandler.getMaxEnergyStored();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

        // If buffer is full, still try to push energy and exit early
        if (!working) {
            pushEnergyToNeighbors();
            return;
        }

        int toAdd = Math.min(getRfTick(), energyHandler.getMaxEnergyStored() - energyHandler.getEnergyStored());
        if (toAdd > 0) {
            energyHandler.addEnergy(toAdd);
        }

        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        // Attempt to push energy out to neighbors
        pushEnergyToNeighbors();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    private void sync() {
        setChanged(getLevel(), getBlockPos(), getBlockState());

        if(level.isClientSide) return;

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public int getRfTick() {
        float mod = 1.0f;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().energy();
        }

        int base = ConfigLoader.getInstance().getSolarConfig(name).energyTick();
        if (base <= 0) return 0;

        float sun = getSolarMultiplier();
        if (sun <= 0.0f) return 0;

        return Math.max(0, Math.round(base * mod * sun));
    }

    private boolean hasSky(BlockPos pos) {
        // Basic check: ensure sky is visible above controller. Avoid rain/thunder reduction for simplicity.
        return level.canSeeSkyFromBelowWater(pos.above());
    }

    /**
     * Computes a smooth 0..1 multiplier for solar strength based on day time and weather.
     * - 0 at sunrise/sunset, 1 at noon
     * - Night produces a small moonlight trickle peaking at midnight
     * - Rain/thunder attenuate output
     */
    public float getSolarMultiplier() {
        if (level == null) return 0.0f;
        if (!level.dimensionType().hasSkyLight()) return 0.0f;
        if (!hasSky(getBlockPos())) return 0.0f;

        long t = level.getDayTime() % 24000L; // 0..23999
        float value;
        if (t < 12000L) {
            // Daytime: cosine lobe from 0 at sunrise (0) to 1 at noon (6000) to 0 at sunset (12000)
            float x = (t - 6000.0f) / 6000.0f; // -1 .. +1
            value = (float) Math.cos(x * (Math.PI / 2.0)); // 0..1..0
            value = Math.max(0.0f, value);
        } else {
            // Nighttime: faint moonlight curve, peaking at midnight (18000)
            float n = (t - 12000.0f);
            float x = (n - 6000.0f) / 6000.0f; // -1 .. +1
            float lobe = (float) Math.cos(x * (Math.PI / 2.0)); // 0..1..0
            lobe = Math.max(0.0f, lobe);
            if (!ConfigLoader.getInstance().SOLAR_ALLOW_MOONLIGHT) {
                value = 0.0f;
            } else {
                value = lobe * Math.max(0.0f, ConfigLoader.getInstance().SOLAR_MOONLIGHT_MAX);
            }
        }

        // Weather attenuation
        if (level.isThundering()) {
            value *= 0.35f;
        } else if (level.isRaining()) {
            value *= 0.6f;
        }

        return Math.max(0.0f, Math.min(1.0f, value));
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        // Support all rotations when matching the multiblock
        modifierMap.clear();
        foundStructure = false;

        for (Rotation rotation : Rotation.values()) {
            RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, rotation);
            if (pattern == null) continue;

            MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, rotation);
            if (result == null || !pattern.ID().equals(structure)) continue;

            foundStructure = true;
            result.blocks().stream().filter(block -> block.getState().getBlock() instanceof ModifierBlock).forEach(block -> {
                ConfigLoader.ModifierConfig modifier = ConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                if (!modifierMap.containsKey(block)) {
                    modifierMap.put(block, modifier);
                }
            });

            break;
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        lazyItemHandler.invalidate();
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    private void pushEnergyToNeighbors() {
        if (level == null || level.isClientSide) return;
        int remaining = energyHandler.getEnergyStored();
        if (remaining <= 0) return;

        for (Direction dir : Direction.values()) {
            if (remaining <= 0) break;
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            LazyOptional<IEnergyStorage> neighborCap = neighborBE.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());
            if (!neighborCap.isPresent()) continue;

            IEnergyStorage neighbor = neighborCap.orElse(null);
            if (neighbor == null) continue;

            int canReceive = neighbor.receiveEnergy(remaining, true);
            if (canReceive > 0) {
                int received = neighbor.receiveEnergy(canReceive, false);
                if (received > 0) {
                    energyHandler.removeEnergy(received);
                    remaining -= received;
                }
            }
        }
    }
}
