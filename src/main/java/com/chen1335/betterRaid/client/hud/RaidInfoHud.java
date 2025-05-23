package com.chen1335.betterRaid.client.hud;

import com.chen1335.betterRaid.Config;
import com.chen1335.betterRaid.network.RaidInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RaidInfoHud implements IGuiOverlay {
    private static RaidInfoHud INSTANCE;
    private Config.HudPosition lastPosition = Config.HudPosition.TOP_LEFT;

    @Nullable
    public RaidInfo raidInfo;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (raidInfo == null) {
            return;
        }
        
        Font font = Minecraft.getInstance().font;
        int i = 0;

        if (lastPosition != Config.hudPosition) {
            Config.applyHudPosition(Config.hudPosition, screenWidth, screenHeight);
            lastPosition = Config.hudPosition;
        }
        
        int baseX = 8 + Config.hudOffsetX;
        int baseY = Config.hudOffsetY;
        
        Difficulty difficulty = Difficulty.byId(raidInfo.difficulty);
        Component hardInfo = Component.translatable("better_raid.raidInfo.difficulty").append(difficulty.getDisplayName()).append(Component.literal(" | ")).append(Component.translatable("better_raid.raidInfo.level")).append(String.valueOf(raidInfo.raidOmenLevel));
        guiGraphics.drawString(font, hardInfo, baseX, baseY + i, 16777215);
        i = i + 9;

        int timeLeftTick = raidInfo.raidCooldownTicks;
        int timeLeftSecond = timeLeftTick / 20;
        Component nextWaveTime = Component.translatable("better_raid.raidInfo.nextWaveTime").append(String.valueOf(timeLeftSecond)).append("s");
        guiGraphics.drawString(font, nextWaveTime, baseX, baseY + i, 16777215);
        i = i + 9;

        TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.BAD_OMEN);
        guiGraphics.blit(baseX - 18, baseY, 0, 18, 18, textureatlassprite);
        Component totalRaidersAliveComponent = Component.translatable("better_raid.raidInfo.totalRaidersAlive").append(String.valueOf(raidInfo.totalRaidersAlive));
        guiGraphics.drawString(font, totalRaidersAliveComponent, baseX, baseY + i, 16777215);
        i = i + 9;

        int numGroups = raidInfo.numGroups;
        if (raidInfo.raidOmenLevel > 1) {
            numGroups++;
        }
        Component waveCount = Component.translatable("better_raid.raidInfo.waveCount").append(raidInfo.groupsSpawned + "/" + numGroups);
        guiGraphics.drawString(font, waveCount, baseX, baseY + i, 16777215);
        i = i + 9;
    }

    public RaidInfoHud() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    public static Optional<RaidInfoHud> getInstance() {
        return Optional.of(INSTANCE);
    }

    public Optional<RaidInfo> geInfo() {
        return Optional.ofNullable(raidInfo);
    }

    public void setInfo(RaidInfo raidInfo) {
        this.raidInfo = raidInfo;
    }
}