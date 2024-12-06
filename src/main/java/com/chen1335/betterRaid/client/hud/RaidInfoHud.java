package com.chen1335.betterRaid.client.hud;

import com.chen1335.betterRaid.network.RaidInfo;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RaidInfoHud implements LayeredDraw.Layer {
    private static RaidInfoHud INSTANCE;

    @Nullable
    public RaidInfo raidInfo;

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (raidInfo == null) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int i = 0;
        Difficulty difficulty = Difficulty.byId(raidInfo.difficulty);
        Component hardInfo = Component.translatable("better_raid.raidInfo.difficulty").append(difficulty.getDisplayName()).append(Component.literal(" | ")).append(Component.translatable("better_raid.raidInfo.level")).append(String.valueOf(raidInfo.raidOmenLevel));
        guiGraphics.drawString(font, hardInfo, 18, i, 16777215);
        i = i + 9;

        int timeLeftTick = raidInfo.raidCooldownTicks;
        int timeLeftSecond = timeLeftTick / 20;
        Component nextWaveTime = Component.translatable("better_raid.raidInfo.nextWaveTime").append(String.valueOf(timeLeftSecond)).append("s");
        guiGraphics.drawString(font, nextWaveTime, 18, i, 16777215);
        i = i + 9;

        TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getMobEffectTextures().get(MobEffects.RAID_OMEN);
        guiGraphics.blit(0, 0, 0, 18, 18, textureatlassprite);
        Component totalRaidersAliveComponent = Component.translatable("better_raid.raidInfo.totalRaidersAlive").append(String.valueOf(raidInfo.totalRaidersAlive));
        guiGraphics.drawString(font, totalRaidersAliveComponent, 18, i, 16777215);
        i = i + 9;

        int numGroups = raidInfo.numGroups;
        if (raidInfo.raidOmenLevel > 1) {
            numGroups++;
        }
        Component waveCount = Component.translatable("better_raid.raidInfo.waveCount").append(raidInfo.groupsSpawned + "/" + numGroups);
        guiGraphics.drawString(font, waveCount, 18, i, 16777215);
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
