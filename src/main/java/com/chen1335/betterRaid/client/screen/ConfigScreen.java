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
//    private HudOffsetSlider hudOffsetXSlider;
//    private HudOffsetSlider hudOffsetYSlider;
    private CycleButton<Config.HudPosition> hudPositionButton;
    
    // 用于临时存储配置值
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
        
        // HUD位置预设选择器
        this.hudPositionButton = this.addRenderableWidget(CycleButton.<Config.HudPosition>builder(pos -> 
                Component.translatable(pos.getTranslationKey()))
                .withValues(Config.HudPosition.values())
                .withInitialValue(tempHudPosition)
                .withTooltip(pos -> Tooltip.create(Component.translatable("better_raid.config.position.tooltip")))
                .create(this.width / 2 - 100, 180, 200, 20,
                        Component.translatable("better_raid.config.position"),
                        (button, value) -> {
                            this.tempHudPosition = value;
                            // 立即应用预设位置
                            Config.applyHudPosition(value, this.width, this.height);
                        }));
        
//        // HUD X偏移滑动条
//        this.hudOffsetXSlider = this.addRenderableWidget(new HudOffsetSlider(
//                this.width / 2 - 100, 230, 200, 20,
//                Component.translatable("better_raid.config.hud_offset_x"),
//                tempHudOffsetX, -100, 100, true));
//
//        // HUD Y偏移滑动条
//        this.hudOffsetYSlider = this.addRenderableWidget(new HudOffsetSlider(
//                this.width / 2 - 100, 280, 200, 20,
//                Component.translatable("better_raid.config.hud_offset_y"),
//                tempHudOffsetY, -100, 100, false));
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
        
        // 保存HUD位置预设并立即应用
        Config.hudPosition = this.tempHudPosition;
        Config.applyHudPosition(this.tempHudPosition, this.width, this.height);
        
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
                
        guiGraphics.drawString(this.font, 
                Component.translatable("better_raid.config.position"), 
                this.width / 2 - 100, 165, 0xFFFFFF);
                
//        guiGraphics.drawString(this.font,
//                Component.translatable("better_raid.config.hud_offset_x"),
//                this.width / 2 - 100, 215, 0xFFFFFF);
//
//        guiGraphics.drawString(this.font,
//                Component.translatable("better_raid.config.hud_offset_y"),
//                this.width / 2 - 100, 265, 0xFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
//    // 自定义滑动条类，用于调整HUD偏移量
//    class HudOffsetSlider extends AbstractSliderButton {
//        private final int minValue;
//        private final int maxValue;
//        private final boolean isXOffset;
//        private int value;
//
//        public HudOffsetSlider(int x, int y, int width, int height, Component message, int defaultValue, int minValue, int maxValue, boolean isXOffset) {
//            super(x, y, width, height, message, (defaultValue - minValue) / (double)(maxValue - minValue));
//            this.minValue = minValue;
//            this.maxValue = maxValue;
//            this.value = defaultValue;
//            this.isXOffset = isXOffset;
//            updateMessage();
//        }
//
//        @Override
//        protected void updateMessage() {
//            this.setMessage(Component.translatable(isXOffset ? "better_raid.config.hud_offset_x.value" : "better_raid.config.hud_offset_y.value", value));
//        }
//
//        @Override
//        protected void applyValue() {
//            // 正确计算滑动条值：将0-1的百分比值映射到minValue-maxValue范围
//            this.value = Mth.floor(this.minValue + (this.maxValue - this.minValue) * this.value);
//            updateMessage();
//        }
//
//        public int getValue() {
//            return this.value;
//        }
//
//        public void setValue(int newValue) {
//            if (newValue >= minValue && newValue <= maxValue) {
//                this.value = newValue;
//                this.value = Mth.clamp(this.value, this.minValue, this.maxValue);
//                // 更新滑动条位置
//                this.setValueRaw((double)(this.value - this.minValue) / (double)(this.maxValue - this.minValue));
//                updateMessage();
//            }
//        }
//
//        @Override
//        public void onClick(double mouseX, double mouseY) {
//            super.onClick(mouseX, mouseY);
//            // 正确计算滑动条值
//            this.value = Mth.floor(this.minValue + (this.maxValue - this.minValue) * this.value);
//            updateMessage();
//        }
//
//        @Override
//        public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
//            super.onDrag(mouseX, mouseY, dragX, dragY);
//            // 正确计算滑动条值
//            this.value = Mth.floor(this.minValue + (this.maxValue - this.minValue) * this.value);
//            updateMessage();
//        }
//
//        @Override
//        public void onRelease(double mouseX, double mouseY) {
//            super.onRelease(mouseX, mouseY);
//            // 正确计算滑动条值
//            this.value = Mth.floor(this.minValue + (this.maxValue - this.minValue) * this.value);
//            updateMessage();
//        }
//    }

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