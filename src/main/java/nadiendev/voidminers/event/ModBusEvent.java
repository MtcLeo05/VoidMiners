package nadiendev.voidminers.event;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.init.ModBlockEntities;
import nadiendev.voidminers.world.multiblock.MinerMultiblocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = VoidMiners.MODID)
public class ModBusEvent {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinerMultiblocks.init();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ModBlockEntities.CONTROLLER_BASE_BE.get(),
            ((o, direction) -> o.getEnergyStorage())
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.CONTROLLER_BASE_BE.get(),
            (be, side) -> be.getItemHandler()
        );
    }
}