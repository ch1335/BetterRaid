package com.chen1335.betterRaid.client.screen;

import com.chen1335.betterRaid.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    private EditBox ticksEditBox;
    private CycleButton<Boolean> autoNextWaveButton;
    private CycleButton<Config.HudPosition> hudPositionButton;

    private boolean tempAutoNextWave;
    private int tempAutoEnterNextWaveTick;
    private int tempHudOffsetX;
    private int tempHudOffsetY;
    private Config.HudPosition tempHudPosition;

    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("better_raid.config.title"));
        this.parentScreen = parentScreen;
        this.tempAutoNextWave = Config.rideAutoNextWave;
        this.tempAutoEnterNextWaveTick = Config.autoEnterNextWaveTick;
        this.tempHudOffsetX = Config.hudOffsetX;
        this.tempHudOffsetY = Config.hudOffsetY;
        this.tempHudPosition = Config.hudPosition;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> {
            this.minecraft.setScreen(parentScreen);
        }).pos(this.width / 2 - 100, this.height - 30).size(200, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("better_raid.config.save"), button -> {
            saveSettings();
            this.minecraft.setScreen(parentScreen);
        }).pos(this.width / 2 - 100, this.height - 55).size(200, 20).build());

        this.autoNextWaveButton = this.addRenderableWidget(CycleButton.onOffBuilder(tempAutoNextWave)
                .displayOnlyValue()
                .withTooltip(on -> Tooltip.create(Component.translatable("better_raid.config.auto_next_wave.tooltip")))
                .create(this.width / 2 - 100, 80, 200, 20, 
                        Component.translatable("better_raid.config.auto_next_wave"), 
                        (button, value) -> this.tempAutoNextWave = value));

        this.ticksEditBox = new EditBox(this.font, this.width / 2 - 100, 130, 200, 20, 
                Component.translatable("better_raid.config.auto_enter_ticks"));
        this.ticksEditBox.setValue(String.valueOf(tempAutoEnterNextWaveTick));
        this.ticksEditBox.setFilter(this::isValidNumber);
        this.addRenderableWidget(this.ticksEditBox);

        this.hudPositionButton = this.addRenderableWidget(CycleButton.<Config.HudPosition>builder(pos -> 
                Component.translatable(pos.getTranslationKey()))
                .withValues(Config.HudPosition.values())
                .withInitialValue(tempHudPosition)
                .withTooltip(pos -> Tooltip.create(Component.translatable("better_raid.config.position.tooltip")))
                .create(this.width / 2 - 100, 180, 200, 20,
                        Component.translatable("better_raid.config.position"),
                        (button, value) -> {
                            this.tempHudPosition = value;
                            Config.applyHudPosition(value, this.width, this.height);
                        }));
    }

    private boolean isValidNumber(String text) {
        if (text.isEmpty()) return true;
        try {
            int value = Integer.parseInt(text);
            return value >= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveSettings() {
        Config.rideAutoNextWave = this.tempAutoNextWave;

        try {
            int ticks = Integer.parseInt(this.ticksEditBox.getValue());
            if (ticks >= 100) {
                Config.autoEnterNextWaveTick = ticks;
            }
        } catch (NumberFormatException ignored) {}

        Config.hudPosition = this.tempHudPosition;
        Config.applyHudPosition(this.tempHudPosition, this.width, this.height);

        Config.saveConfig();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.auto_next_wave"), 
                this.width / 2 - 100, 65, 0xFFFFFF);
        
        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.auto_enter_ticks"), 
                this.width / 2 - 100, 115, 0xFFFFFF);
                
        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.position"), 
                this.width / 2 - 100, 165, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, screen) -> new ConfigScreen(screen)
                )
        );
    }
}