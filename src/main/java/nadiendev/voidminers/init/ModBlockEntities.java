package nadiendev.voidminers.init;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.world.block.entity.ControllerBaseBE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VoidMiners.MODID);


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerBaseBE>> CONTROLLER_BASE_BE = 
        BLOCK_ENTITIES.register("controller_base",
            () -> BlockEntityType.Builder.of(
                ControllerBaseBE::new,
                CrystalSet.RUBETINE.MINER_CONTROLLER.get(),
                CrystalSet.AURANTIUM.MINER_CONTROLLER.get(),
                CrystalSet.CITRINETINE.MINER_CONTROLLER.get(),
                CrystalSet.VERDIUM.MINER_CONTROLLER.get(),
                CrystalSet.AZURINE.MINER_CONTROLLER.get(),
                CrystalSet.CAERIUM.MINER_CONTROLLER.get(),
                CrystalSet.AMETHYSTINE.MINER_CONTROLLER.get(),
                CrystalSet.ROSARIUM.MINER_CONTROLLER.get()
            ).build(null)
        );
}