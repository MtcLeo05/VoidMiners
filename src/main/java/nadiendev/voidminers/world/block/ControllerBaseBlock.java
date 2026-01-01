package nadiendev.voidminers.world.block;

import nadiendev.voidminers.world.block.entity.ControllerBaseBE;
import nadiendev.voidminers.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    final String name;

    public ControllerBaseBlock(Properties pProperties, ResourceLocation structure, String name) {
        super(pProperties);
        this.structure = structure;
        this.name = name;
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ControllerBaseBE controllerBE) {
                controllerBE.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerBaseBE(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        ControllerBaseBE blockEntity = (ControllerBaseBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (pPlayer.isCrouching()) {
            if (blockEntity != null) {
                blockEntity.updateShowStructure();
            }
            return InteractionResult.CONSUME;
        }

        if (blockEntity != null) {
            for (Component component : blockEntity.getInteractionTooltip()) {
                pPlayer.displayClientMessage(component, false);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        ControllerBaseBE controller = ((ControllerBaseBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((ControllerBaseBE) this.newBlockEntity(pPos, pState));
        }

        if (controller != null) {
            controller.setup(structure, name);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return ((level, blockPos, blockState, be) -> {
            if (be instanceof ControllerBaseBE controllerBE) {
                controllerBE.tick(pLevel, blockPos, blockState, structure, name);
            }
        });
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.or(
            ShapeUtil.shapeFromDimension(0, 0, 0, 16, 2, 16),
            ShapeUtil.shapeFromDimension(2, 2, 2, 12, 12, 12),
            ShapeUtil.shapeFromDimension(7, 0f, 1, 2, 15f, 14),
            ShapeUtil.shapeFromDimension(1, 7, 9, 6, 2, 6),
            ShapeUtil.shapeFromDimension(1, 7, 1, 6, 2, 6),
            ShapeUtil.shapeFromDimension(9, 7, 9, 6, 2, 6),
            ShapeUtil.shapeFromDimension(9, 7, 1, 6, 2, 6),
            ShapeUtil.shapeFromDimension(1, 0f, 7, 6, 15f, 2),
            ShapeUtil.shapeFromDimension(9, 0f, 7, 6, 15f, 2)
        );
    }
}