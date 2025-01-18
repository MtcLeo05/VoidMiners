package com.leo.voidminers.block.entity;

import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ModifierBE extends BlockEntity {
    private String name;
    private ModifierType type;

    public ModifierBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MODIFIER_BE.get(), pPos, pBlockState);
    }

    public void setup(String name, ModifierType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains("name")) {
            name = pTag.getString("name");
        }

        if (pTag.contains("type")) {
            type = ModifierType.getFromName(pTag.getString("type"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (name != null && !name.isEmpty() && !name.isBlank()) {
            pTag.putString("name", name);
        }

        if (type != null) {
            pTag.putString("type", type.type);
        }
    }

    public ConfigLoader.ModifierConfig getModifiers() {
        if (name != null && type != null) {
            return ConfigLoader.getInstance().getModifierConfig(name, type.type);
        }

        return new ConfigLoader.ModifierConfig(1, 1, 1);
    }

    public enum ModifierType {
        ENERGY("energy"),
        SPEED("speed"),
        ITEM("item"),
        NULL("null");

        public final String type;

        ModifierType(String type) {
            this.type = type;
        }

        public static ModifierType getFromName(String name) {
            return switch (name.toLowerCase()) {
                case "energy" -> ENERGY;
                case "speed" -> SPEED;
                case "item" -> ITEM;
                default -> null;
            };
        }
    }
}
