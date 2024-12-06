package com.chen1335.betterRaid;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = BetterRaid.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue RIDE_AUTO_NEXT_WAVE = BUILDER
            .comment("Whether auto enter next wave")
            .define("rideAutoNextWave", true);

    private static final ModConfigSpec.IntValue AUTO_ENTER_NEXT_WAVE_TICK = BUILDER
            .comment("the tick of auto enter next wave")
            .defineInRange("autoEnterNextWaveTick", 3600, 100, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean rideAutoNextWave = true;
    public static int autoEnterNextWaveTick = 0;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        rideAutoNextWave = RIDE_AUTO_NEXT_WAVE.get();
        autoEnterNextWaveTick = AUTO_ENTER_NEXT_WAVE_TICK.get();
    }
}
