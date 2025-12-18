package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.config.ConfigReloadListener;
import com.leo.voidminers.network.ModNetwork;
import com.leo.voidminers.network.packet.SyncConfigS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = VoidMiners.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ForgeBusEvent {
    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ConfigReloadListener());
    }

    @SubscribeEvent
    public static void onReload(LevelTickEvent.Post event) {
        if(!(event.getLevel() instanceof ServerLevel sLevel)) return;

        int ticks = sLevel.getServer().getTickCount();
        if(ticks % 100 != 0) return;

        ModNetwork.sendToAllPlayers(
            new SyncConfigS2CPacket(
                ConfigLoader.getInstance().MINER_CONFIGS
            )
        );
    }
}