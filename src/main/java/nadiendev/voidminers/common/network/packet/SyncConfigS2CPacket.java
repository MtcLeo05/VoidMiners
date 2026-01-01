package nadiendev.voidminers.common.network.packet;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.config.ConfigLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;


public record SyncConfigS2CPacket(Map<String, ConfigLoader.MinerConfig> minerConfigs) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncConfigS2CPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "sync_config"));

    public static final StreamCodec<ByteBuf, SyncConfigS2CPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            ConfigLoader.MinerConfig.STREAM_CODEC
        ),
        SyncConfigS2CPacket::minerConfigs,
        SyncConfigS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncConfigS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ConfigLoader.getInstance().MINER_CONFIGS = packet.minerConfigs();
        });
    }
}