package nadiendev.voidminers.world.item;

import nadiendev.voidminers.world.block.entity.ControllerBaseBE;
import nadiendev.voidminers.world.multiblock.MinerMultiblocks;
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

public class StructureHelper extends Item {
    public StructureHelper(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getPlayer() == null) return InteractionResult.PASS;

        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }

        BlockEntity entity = level.getBlockEntity(pos);

        if(!(entity instanceof ControllerBaseBE controller)) return InteractionResult.PASS;
        ResourceLocation structure = controller.getStructure();
        RegisteredMultiBlockPattern multiBlock = MinerMultiblocks.MANAGER.getStructure(structure);

        if (!((ServerPlayer) pContext.getPlayer()).gameMode.isCreative()) {
            return InteractionResult.CONSUME;
        }

        multiBlock.pattern().construct(level, pos);
        return InteractionResult.CONSUME;
    }
}
