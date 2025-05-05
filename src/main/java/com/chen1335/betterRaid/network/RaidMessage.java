package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.client.hud.RaidInfoHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RaidMessage {
    private final int messageId;

    public RaidMessage(int messageId) {
        this.messageId = messageId;
    }

    public static void encode(RaidMessage message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.messageId);
    }

    public static RaidMessage decode(FriendlyByteBuf buffer) {
        return new RaidMessage(buffer.readVarInt());
    }

    public static void handle(RaidMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() == null && context.getDirection().getReceptionSide().isClient()) {
                if (message.messageId == 0) {
                    RaidInfoHud.getInstance().ifPresent(raidInfoHud -> {
                        raidInfoHud.setInfo(null);
                    });
                }
            }
        });
        context.setPacketHandled(true);
    }
}