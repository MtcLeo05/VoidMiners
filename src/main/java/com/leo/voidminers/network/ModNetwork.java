package com.leo.voidminers.network;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.network.packet.SyncConfigS2CPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel CHANNEL;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    /**
     * Registers the network channel for the mod.
     */
    public static void register() {
        CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(VoidMiners.MODID, "messages"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

        CHANNEL.messageBuilder(SyncConfigS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncConfigS2CPacket::new)
            .encoder(SyncConfigS2CPacket::toBytes)
            .consumerMainThread(SyncConfigS2CPacket::handle)
            .add();

    }

    public static <T> void sendToAllPlayers(T message) {
        if(CHANNEL == null) return;

        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

}
