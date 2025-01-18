package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.config.ConfigReloadListener;
import com.leo.voidminers.network.ModNetwork;
import com.leo.voidminers.network.packet.SyncConfigS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEvent {
    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ConfigReloadListener());
    }

    @SubscribeEvent
    public static void onReload(TickEvent.LevelTickEvent event) {
        if(!(event.level instanceof ServerLevel sLevel)) return;

        int ticks = sLevel.getServer().getTickCount();
        if(ticks % 100 != 0) return;

        ModNetwork.sendToAllPlayers(
            new SyncConfigS2CPacket(
                ConfigLoader.getInstance().MINE_PREVIOUS_TIER,
                ConfigLoader.getInstance().MINER_CONFIGS
            )
        );
    }
}
