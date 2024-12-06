package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BetterRaid.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Payloads {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(RaidInfo.TYPE, RaidInfo.STREAM_CODEC, RaidInfo::clientHandler);
        registrar.playToClient(RaidMessage.TYPE, RaidMessage.STREAM_CODEC, RaidMessage::clientHandler);
        registrar.playToClient(SimpleDataSetter.TYPE, SimpleDataSetter.STREAM_CODEC, SimpleDataSetter::clientHandler);
    }
}
