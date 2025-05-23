package com.chen1335.betterRaid;

import com.chen1335.betterRaid.client.screen.ConfigScreen;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;
import com.chen1335.betterRaid.network.Payloads;

@Mod(BetterRaid.MODID)
public class BetterRaid {
    public static final String MODID = "better_raid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BetterRaid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Payloads.register();
        LOGGER.info("Network register successful");
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ConfigScreen.register();
        }
    }
}
