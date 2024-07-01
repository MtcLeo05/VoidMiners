package com.leo.voidminers.block;

import com.leo.voidminers.block.entity.ControllerBaseBE;
import com.leo.voidminers.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ControllerBaseBlock extends BaseTransparentBlock implements EntityBlock {
    final ResourceLocation structure;
    final int duration;
    final int rfTick;

    public ControllerBaseBlock(Properties pProperties, ResourceLocation structure, int duration, int rfTick) {
        super(pProperties);
        this.structure = structure;
        this.duration = duration;
        this.rfTick = rfTick;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            ((ControllerBaseBE) blockEntity).drops();
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerBaseBE(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ControllerBaseBE blockEntity = (ControllerBaseBE) pLevel.getBlockEntity(pPos);

        if (!pLevel.isClientSide) {

            if (pPlayer.isCrouching()) {
                blockEntity.updateShowStructure();
            } else {
                for (Component component : blockEntity.getInteractionTooltip()) {
                    pPlayer.displayClientMessage(
                        component,
                        false
                    );
                }
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        ControllerBaseBE controller = ((ControllerBaseBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((ControllerBaseBE) this.newBlockEntity(pPos, pState));
        }

        controller.setup(structure, duration, rfTick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return ((level, blockPos, blockState, be) -> ((ControllerBaseBE) be).tick(pLevel, blockPos, blockState));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.or(
            ShapeUtil.shapeFromDimension(0, 15, 0, 16, 1, 16),
            ShapeUtil.shapeFromDimension(1.5f, 2, 1.5f, 13, 13, 13),
            ShapeUtil.shapeFromDimension(0, 0, 0, 16, 2, 16),
            ShapeUtil.shapeFromDimension(15, 2, 15, 1, 13, 1),
            ShapeUtil.shapeFromDimension(0, 2, 15, 1, 13, 1),
            ShapeUtil.shapeFromDimension(15, 2, 0, 1, 13, 1),
            ShapeUtil.shapeFromDimension(0, 2, 0, 1, 13, 1)
        );
    }
}
