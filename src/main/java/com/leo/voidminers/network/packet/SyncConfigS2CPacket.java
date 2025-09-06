package com.leo.voidminers.network.packet;

import com.leo.voidminers.config.ConfigLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This Server to Client packet will transmit the configs from server to client
 */
public class SyncConfigS2CPacket {

    private final Map<String, ConfigLoader.MinerConfig> minerConfigs;
    private final Map<String, ConfigLoader.SolarConfig> solarConfigs;
    private final boolean allowMoonlight;
    private final float moonlightMax;

    public SyncConfigS2CPacket(Map<String, ConfigLoader.MinerConfig> minerConfigs,
                               Map<String, ConfigLoader.SolarConfig> solarConfigs,
                               boolean allowMoonlight,
                               float moonlightMax) {
        this.minerConfigs = minerConfigs;
        this.solarConfigs = solarConfigs;
        this.allowMoonlight = allowMoonlight;
        this.moonlightMax = moonlightMax;
    }

    public SyncConfigS2CPacket(FriendlyByteBuf buf) {
        int minerEntries = buf.readInt();

        minerConfigs = new HashMap<>();
        for (int i = 0; i < minerEntries; i++) {
            String key = buf.readUtf();
            ConfigLoader.MinerConfig value = ConfigLoader.MinerConfig.fromBuf(buf);
            minerConfigs.put(key, value);
        }

        int solarEntries = buf.readInt();
        solarConfigs = new HashMap<>();
        for (int i = 0; i < solarEntries; i++) {
            String key = buf.readUtf();
            ConfigLoader.SolarConfig value = ConfigLoader.SolarConfig.fromBuf(buf);
            solarConfigs.put(key, value);
        }

        allowMoonlight = buf.readBoolean();
        moonlightMax = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(minerConfigs.size());
        minerConfigs.forEach((key, value) -> {
            buf.writeUtf(key);
            value.toBuf(buf);
        });

        buf.writeInt(solarConfigs.size());
        solarConfigs.forEach((key, value) -> {
            buf.writeUtf(key);
            value.toBuf(buf);
        });

        buf.writeBoolean(allowMoonlight);
        buf.writeFloat(moonlightMax);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ConfigLoader.getInstance().MINER_CONFIGS = minerConfigs;
        ConfigLoader.getInstance().SOLAR_CONFIGS = solarConfigs;
        ConfigLoader.getInstance().SOLAR_ALLOW_MOONLIGHT = allowMoonlight;
        ConfigLoader.getInstance().SOLAR_MOONLIGHT_MAX = moonlightMax;
    }
}
