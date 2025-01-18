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

    private final boolean minePreviousTier;
    private final Map<String, ConfigLoader.MinerConfig> minerConfigs;

    public SyncConfigS2CPacket(boolean minePreviousTier, Map<String, ConfigLoader.MinerConfig> minerConfigs) {
        this.minePreviousTier = minePreviousTier;
        this.minerConfigs = minerConfigs;
    }

    public SyncConfigS2CPacket(FriendlyByteBuf buf) {
        this.minePreviousTier = buf.readBoolean();
        int entries = buf.readInt();

        minerConfigs = new HashMap<>();

        for (int i = 0; i < entries; i++) {
            String key = buf.readUtf();
            ConfigLoader.MinerConfig value = ConfigLoader.MinerConfig.fromBuf(buf);

            minerConfigs.put(key, value);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(minePreviousTier);

        buf.writeInt(minerConfigs.size());

        minerConfigs.forEach((key, value) -> {
            buf.writeUtf(key);
            value.toBuf(buf);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        ConfigLoader.getInstance().MINE_PREVIOUS_TIER = minePreviousTier;
        ConfigLoader.getInstance().MINER_CONFIGS = minerConfigs;
    }
}
