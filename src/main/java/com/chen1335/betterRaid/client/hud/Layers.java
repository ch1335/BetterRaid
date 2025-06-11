package com.chen1335.betterRaid.client.hud;

import com.chen1335.betterRaid.BetterRaid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterRaid.MODID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Layers {

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(BetterRaid.MODID + "raid_info", new RaidInfoHud());
        BetterRaid.LOGGER.info(" Registering RaidInfoHud overlay");
    }
}