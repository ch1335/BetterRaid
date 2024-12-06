package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import com.chen1335.betterRaid.client.hud.RaidInfoHud;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RaidMessage(int messageId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RaidMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BetterRaid.MODID, "raid_message"));


    public static final StreamCodec<ByteBuf, RaidMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            RaidMessage::messageId,
            RaidMessage::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void clientHandler(IPayloadContext iPayloadContext) {
        if (messageId == 0) {
            RaidInfoHud.getInstance().ifPresent(raidInfoHud -> {
                raidInfoHud.setInfo(null);
            });
        }
    }
}
