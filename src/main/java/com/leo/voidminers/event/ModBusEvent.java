package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.multiblock.MinerMultiblocks;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.List;

import static com.leo.voidminers.config.ServerConfig.configData;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvent {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinerMultiblocks.init();

        List<Block> controllers = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> block instanceof ControllerBaseBlock).toList();

        ModBlockEntities.CONTROLLER_BASE_BE.get().validBlocks = new HashSet<>(controllers);

        List<Block> modifiers = ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> block instanceof ModifierBlock).toList();

        ModBlockEntities.MODIFIER_BE.get().validBlocks = new HashSet<>(modifiers);
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        configData = event.getConfig().getConfigData();
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        configData = event.getConfig().getConfigData();
    }
}
