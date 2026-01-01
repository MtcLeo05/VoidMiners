package nadiendev.voidminers;

import nadiendev.voidminers.config.ConfigLoader;
import nadiendev.voidminers.init.*;
import nadiendev.voidminers.world.multiblock.MinerMultiblocks;
import nadiendev.voidminers.common.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(VoidMiners.MODID)
public class VoidMiners {
    public static final String MODID = "voidminers";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public VoidMiners(IEventBus modEventBus, ModContainer container) {
        ConfigLoader.getInstance().load();
        CrystalSet.initSets();
        
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
    }

    private void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        ModNetwork.register(event);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(this::setupRenders);
    }

    private void setupRenders() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GLASS_PANEL.get(), RenderType.translucent());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModRarities.init();
            MinerMultiblocks.init(); 
            LOGGER.info("Multiblocks initialized successfully!");
        });
    }
}