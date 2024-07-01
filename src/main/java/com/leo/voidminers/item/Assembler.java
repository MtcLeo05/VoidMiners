package com.leo.voidminers.item;

import com.leo.voidminers.block.entity.ControllerBaseBE;
import com.leo.voidminers.multiblock.MinerMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;

public class Assembler extends Item {
    public Assembler(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (level instanceof ServerLevel) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ControllerBaseBE controller) {
                ServerPlayer player = ((ServerPlayer) pContext.getPlayer());
                ResourceLocation structure = controller.getStructure();
                RegisteredMultiBlockPattern multiBlock = MinerMultiblocks.MANAGER.getStructure(structure);
                if (player.gameMode.isCreative()) {
                    multiBlock.pattern().construct(level, pos);
                }
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.PASS;
    }
}
