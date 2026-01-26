package nadiendev.voidminers.common.compat.jade;

import nadiendev.voidminers.world.block.entity.ControllerBaseBE;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(VoidMinerProvider.INSTANCE, ControllerBaseBE.class);
    }
    
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Registrar para todos los bloques - Jade filtrará automáticamente por el BlockEntity
        registration.registerBlockComponent(VoidMinerProvider.INSTANCE, Block.class);
    }
}