package com.leo.voidminers.block.entity;

import com.leo.voidminers.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ModifierBE extends BlockEntity {

    private float[] modifiers = {1, 1, 1};

    public ModifierBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MODIFIER_BE.get(), pPos, pBlockState);
    }

    public void setup(float[] modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        CompoundTag modifierTag = pTag.getCompound("modifiers");
        List<Float> modList = new ArrayList<>();

        for (String key : modifierTag.getAllKeys()) {
            modList.add(
                modifierTag.getFloat(key)
            );
        }

        for (int i = 0; i < modList.size(); i++) {
            modifiers[i] = modList.get(i);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag tag = new CompoundTag();

        for (int i = 0; i < modifiers.length; i++) {
            tag.putFloat("" + i, modifiers[i]);
        }

        pTag.put("modifiers", tag);
    }

    public float[] getModifiers() {
        return modifiers;
    }
}
