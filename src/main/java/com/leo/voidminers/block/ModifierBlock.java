package com.leo.voidminers.block;

import com.leo.voidminers.block.entity.ModifierBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ModifierBlock extends Block implements EntityBlock {
    public String name;
    public ModifierBE.ModifierType type;

    public ModifierBlock(Properties pProperties, String name, ModifierBE.ModifierType type) {
        super(pProperties);
        this.name = name;
        this.type = type;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        ModifierBE controller = ((ModifierBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((ModifierBE) this.newBlockEntity(pPos, pState));
        }

        controller.setup(name, type);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ModifierBE(pPos, pState);
    }
}
