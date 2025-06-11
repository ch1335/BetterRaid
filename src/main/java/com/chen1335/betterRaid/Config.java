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
            
    private static final ForgeConfigSpec.IntValue HUD_OFFSET_X = BUILDER
            .comment("X offset for the raid info HUD")
            .defineInRange("hudOffsetX", 10, -10000, 10000);
            
    private static final ForgeConfigSpec.IntValue HUD_OFFSET_Y = BUILDER
            .comment("Y offset for the raid info HUD")
            .defineInRange("hudOffsetY", 0, -10000, 10000);
            
    private static final ForgeConfigSpec.EnumValue<HudPosition> HUD_POSITION = BUILDER
            .comment("Preset position for the raid info HUD")
            .defineEnum("hudPosition", HudPosition.TOP_LEFT);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean rideAutoNextWave = true;
    public static int autoEnterNextWaveTick = 0;
    public static int hudOffsetX = 0;
    public static int hudOffsetY = 0;
    public static HudPosition hudPosition = HudPosition.TOP_LEFT;

    public enum HudPosition {
        TOP_LEFT("better_raid.config.position.top_left"),
        TOP_CENTER("better_raid.config.position.top_center"),
        TOP_RIGHT("better_raid.config.position.top_right"),
        BOTTOM_LEFT("better_raid.config.position.bottom_left"),
        BOTTOM_CENTER("better_raid.config.position.bottom_center"),
        BOTTOM_RIGHT("better_raid.config.position.bottom_right");
        
        private final String translationKey;
        
        HudPosition(String translationKey) {
            this.translationKey = translationKey;
        }
        
        public String getTranslationKey() {
            return translationKey;
        }
    }

    private static ModConfig CONFIG;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getModId().equals(BetterRaid.MODID)) {
            CONFIG = config;
            rideAutoNextWave = RIDE_AUTO_NEXT_WAVE.get();
            autoEnterNextWaveTick = AUTO_ENTER_NEXT_WAVE_TICK.get();
            hudOffsetX = HUD_OFFSET_X.get();
            hudOffsetY = HUD_OFFSET_Y.get();
            hudPosition = HUD_POSITION.get();
        }
    }

    public static void saveConfig() {
        if (CONFIG != null) {
            RIDE_AUTO_NEXT_WAVE.set(rideAutoNextWave);
            AUTO_ENTER_NEXT_WAVE_TICK.set(autoEnterNextWaveTick);
            HUD_OFFSET_X.set(hudOffsetX);
            HUD_OFFSET_Y.set(hudOffsetY);
            HUD_POSITION.set(hudPosition);
            CONFIG.save();
        }
    }

    public static void applyHudPosition(HudPosition position, int screenWidth, int screenHeight) {
        hudPosition = position;

        switch (position) {
            case TOP_LEFT:
                hudOffsetX = 8;
                hudOffsetY = 0;
                break;
            case TOP_CENTER:
                hudOffsetX = screenWidth / 2 - 47;
                hudOffsetY = 0;
                break;
            case TOP_RIGHT:
                hudOffsetX = screenWidth - 101;
                hudOffsetY = 0;
                break;
            case BOTTOM_LEFT:
                hudOffsetX = 10;
                hudOffsetY = screenHeight - 40;
                break;
            case BOTTOM_CENTER:
                hudOffsetX = screenWidth / 2 - 47;
                hudOffsetY = screenHeight - 40;
                break;
            case BOTTOM_RIGHT:
                hudOffsetX = screenWidth - 101;
                hudOffsetY = screenHeight - 40;
                break;
        }
    }
}
