package com.chen1335.betterRaid.client.screen;

import com.chen1335.betterRaid.BetterRaid;
import com.chen1335.betterRaid.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    private EditBox ticksEditBox;
    private CycleButton<Boolean> autoNextWaveButton;
    
    // 用于临时存储配置值
    private boolean tempAutoNextWave;
    private int tempAutoEnterNextWaveTick;

    public ConfigScreen(Screen parentScreen) {
        super(Component.translatable("better_raid.config.title"));
        this.parentScreen = parentScreen;
        this.tempAutoNextWave = Config.rideAutoNextWave;
        this.tempAutoEnterNextWaveTick = Config.autoEnterNextWaveTick;
    }

    @Override
    protected void init() {
        super.init();
        
        // 添加返回按钮
        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> {
            this.minecraft.setScreen(parentScreen);
        }).pos(this.width / 2 - 100, this.height - 30).size(200, 20).build());
        
        // 添加保存按钮
        this.addRenderableWidget(Button.builder(Component.translatable("better_raid.config.save"), button -> {
            saveSettings();
            this.minecraft.setScreen(parentScreen);
        }).pos(this.width / 2 - 100, this.height - 55).size(200, 20).build());
        
        // 自动进入下一波开关
        this.autoNextWaveButton = this.addRenderableWidget(CycleButton.onOffBuilder(tempAutoNextWave)
                .displayOnlyValue()
                .withTooltip(on -> Tooltip.create(Component.translatable("better_raid.config.auto_next_wave.tooltip")))
                .create(this.width / 2 - 100, 80, 200, 20, 
                        Component.translatable("better_raid.config.auto_next_wave"), 
                        (button, value) -> this.tempAutoNextWave = value));
        
        // 自动进入下一波的tick数输入框
        this.ticksEditBox = new EditBox(this.font, this.width / 2 - 100, 130, 200, 20, 
                Component.translatable("better_raid.config.auto_enter_ticks"));
        this.ticksEditBox.setValue(String.valueOf(tempAutoEnterNextWaveTick));
        this.ticksEditBox.setFilter(this::isValidNumber);
        this.addRenderableWidget(this.ticksEditBox);
    }

    private boolean isValidNumber(String text) {
        if (text.isEmpty()) return true;
        try {
            int value = Integer.parseInt(text);
            return value >= 100; // 确保值大于等于100
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveSettings() {
        // 保存自动进入下一波设置
        Config.rideAutoNextWave = this.tempAutoNextWave;
        
        // 保存tick数设置
        try {
            int ticks = Integer.parseInt(this.ticksEditBox.getValue());
            if (ticks >= 100) {
                Config.autoEnterNextWaveTick = ticks;
            }
        } catch (NumberFormatException ignored) {
            // 如果输入无效，保持原值不变
        }
        
        // 触发配置保存
        Config.saveConfig();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        // 绘制标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制选项标签
        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.auto_next_wave"), 
                this.width / 2 - 100, 65, 0xFFFFFF);
        
        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.auto_enter_ticks"), 
                this.width / 2 - 100, 115, 0xFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // 注册配置屏幕
    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, screen) -> new ConfigScreen(screen)
                )
        );
    }
}