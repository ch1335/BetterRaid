package com.chen1335.betterRaid;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;


@EventBusSubscriber(modid = BetterRaid.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue RIDE_AUTO_NEXT_WAVE = BUILDER
            .comment("Whether auto enter next wave")
            .define("rideAutoNextWave", true);

    private static final ForgeConfigSpec.IntValue AUTO_ENTER_NEXT_WAVE_TICK = BUILDER
            .comment("the tick of auto enter next wave")
            .defineInRange("autoEnterNextWaveTick", 3600, 100, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean rideAutoNextWave = true;
    public static int autoEnterNextWaveTick = 0;
    
    // 保存当前配置的引用，用于保存
    private static ModConfig CONFIG;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getModId().equals(BetterRaid.MODID)) {
            CONFIG = config;
            rideAutoNextWave = RIDE_AUTO_NEXT_WAVE.get();
            autoEnterNextWaveTick = AUTO_ENTER_NEXT_WAVE_TICK.get();
        }
    }
    
    // 保存配置到文件
    public static void saveConfig() {
        if (CONFIG != null) {
            RIDE_AUTO_NEXT_WAVE.set(rideAutoNextWave);
            AUTO_ENTER_NEXT_WAVE_TICK.set(autoEnterNextWaveTick);
            CONFIG.save();
        }
    }
}
