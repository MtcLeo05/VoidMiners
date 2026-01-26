package nadiendev.voidminers.common.network;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.common.network.packet.SyncConfigS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {

    
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VoidMiners.MODID)
            .versioned("1.0.0")
            .optional();

        registrar.playToClient(
            SyncConfigS2CPacket.TYPE,
            SyncConfigS2CPacket.STREAM_CODEC,
            SyncConfigS2CPacket::handle
        );
    }

    public static void sendToAllPlayers(SyncConfigS2CPacket message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static void sendToPlayer(ServerPlayer player, SyncConfigS2CPacket message) {
        PacketDistributor.sendToPlayer(player, message);
    }
}