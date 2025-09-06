package com.leo.voidminers.block;

import com.leo.voidminers.block.entity.SolarControllerBE;
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
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import com.leo.voidminers.menu.SolarControllerMenu;

public class SolarControllerBlock extends BaseTransparentBlock implements EntityBlock {
    final ResourceLocation structure;
    final String name;

    // Precompute and cache the shape once to avoid repeated expensive unions
    private static final VoxelShape SHAPE = Shapes.or(
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

    public SolarControllerBlock(Properties pProperties, ResourceLocation structure, String name) {
        super(pProperties);
        this.structure = structure;
        this.name = name;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SolarControllerBE solar) {
                solar.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolarControllerBE(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        SolarControllerBE blockEntity = (SolarControllerBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        if (pPlayer.isCrouching()) {
            blockEntity.updateShowStructure();
            return InteractionResult.CONSUME;
        }

        // Open the Solar Controller screen
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                (id, inv, player) -> new SolarControllerMenu(id, inv, blockEntity),
                Component.translatable("screen.voidminers.solar.title")
            );
            NetworkHooks.openScreen(serverPlayer, provider, pPos);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        SolarControllerBE controller = ((SolarControllerBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((SolarControllerBE) this.newBlockEntity(pPos, pState));
        }

        controller.setup(structure, name);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return ((level, blockPos, blockState, be) -> ((SolarControllerBE) be).tick(pLevel, blockPos, blockState, structure, name));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
