package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = BetterRaid.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Payloads {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(BetterRaid.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, RaidInfo.class, RaidInfo::encode, RaidInfo::decode, RaidInfo::handle);
        INSTANCE.registerMessage(id++, RaidMessage.class, RaidMessage::encode, RaidMessage::decode, RaidMessage::handle);
        INSTANCE.registerMessage(id++, SimpleDataSetter.class, SimpleDataSetter::encode, SimpleDataSetter::decode, SimpleDataSetter::handle);
    }

    public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
        INSTANCE.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}