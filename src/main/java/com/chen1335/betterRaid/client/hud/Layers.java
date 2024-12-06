package com.chen1335.betterRaid.client.hud;

import com.chen1335.betterRaid.BetterRaid;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = BetterRaid.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Layers {

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(BetterRaid.MODID, "raid_info"), new RaidInfoHud());
    }
}
