package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.entity.renderer.ControllerRenderer;
import com.leo.voidminers.init.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = VoidMiners.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBusClientEvent {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CONTROLLER_BASE_BE.get(), ControllerRenderer::new);
    }
}