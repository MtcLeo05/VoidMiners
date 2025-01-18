package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ConfigReloadListener;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.multiblock.MinerMultiblocks;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;

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
}
