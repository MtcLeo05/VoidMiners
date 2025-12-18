package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.multiblock.MinerMultiblocks;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = VoidMiners.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvent {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinerMultiblocks.init();

       
        List<Block> controllers = ModBlocks.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(block -> block instanceof ControllerBaseBlock)
                .map(block -> (Block) block)
                .collect(Collectors.toList());

        
        // ModBlockEntities.CONTROLLER_BASE_BE.get().validBlocks = new HashSet<>(controllers);
        
      
    }
}