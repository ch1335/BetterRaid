package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import com.chen1335.betterRaid.client.hud.RaidInfoHud;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class RaidInfo implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RaidInfo> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BetterRaid.MODID, "raid_info"));
    public int totalRaidersAlive;
    public int groupsSpawned;
    public int numGroups;
    public int raidOmenLevel;
    public int difficulty;
    public int raidCooldownTicks;

    public static final SimpleDataSetter.DataSetterType<Integer> RAID_COOLDOWN_TICKS_SETTER = SimpleDataSetter.DataSetterType.create(ByteBufCodecs.VAR_INT, integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.raidCooldownTicks = integer);
    });
    public static final SimpleDataSetter.DataSetterType<Integer> TOTAL_RAIDERS_ALIVE_SETTER = SimpleDataSetter.DataSetterType.create(ByteBufCodecs.VAR_INT, integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.totalRaidersAlive = integer);
    });
    public static final SimpleDataSetter.DataSetterType<Integer> GROUPS_SPAWNED_SETTER = SimpleDataSetter.DataSetterType.create(ByteBufCodecs.VAR_INT, integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.groupsSpawned = integer);
    });
    public RaidInfo(int totalRaidersAlive, int groupsSpawned, int numGroups, int raidOmenLevel, int difficulty, int raidCooldownTicks) {
        this.totalRaidersAlive = totalRaidersAlive;
        this.groupsSpawned = groupsSpawned;
        this.numGroups = numGroups;
        this.raidOmenLevel = raidOmenLevel;
        this.difficulty = difficulty;
        this.raidCooldownTicks = raidCooldownTicks;
    }

    public static final StreamCodec<ByteBuf, RaidInfo> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                ByteBufCodecs.VAR_INT.encode(buffer, value.totalRaidersAlive);
                ByteBufCodecs.VAR_INT.encode(buffer, value.groupsSpawned);
                ByteBufCodecs.VAR_INT.encode(buffer, value.numGroups);
                ByteBufCodecs.VAR_INT.encode(buffer, value.raidOmenLevel);
                ByteBufCodecs.VAR_INT.encode(buffer, value.difficulty);
                ByteBufCodecs.VAR_INT.encode(buffer, value.raidCooldownTicks);
            },
            buffer -> {
                Integer totalRaidersAlive = ByteBufCodecs.VAR_INT.decode(buffer);
                Integer groupsSpawned = ByteBufCodecs.VAR_INT.decode(buffer);
                Integer numGroups = ByteBufCodecs.VAR_INT.decode(buffer);
                Integer raidOmenLevel = ByteBufCodecs.VAR_INT.decode(buffer);
                Integer difficulty = ByteBufCodecs.VAR_INT.decode(buffer);
                Integer raidCooldownTicks = ByteBufCodecs.VAR_INT.decode(buffer);
                return new RaidInfo(totalRaidersAlive, groupsSpawned, numGroups, raidOmenLevel, difficulty, raidCooldownTicks);
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void clientHandler(IPayloadContext iPayloadContext) {
        if (iPayloadContext.player() instanceof LocalPlayer) {
            RaidInfoHud.getInstance().ifPresent(raidInfoHud -> raidInfoHud.setInfo(this));
        }
    }
}
