package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import com.chen1335.betterRaid.client.hud.RaidInfoHud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RaidInfo {
    public int totalRaidersAlive;
    public int groupsSpawned;
    public int numGroups;
    public int raidOmenLevel;
    public int difficulty;
    public int raidCooldownTicks;

    public static final SimpleDataSetter.DataSetterType<Integer> RAID_COOLDOWN_TICKS_SETTER = SimpleDataSetter.DataSetterType.create(integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.raidCooldownTicks = integer);
    });
    public static final SimpleDataSetter.DataSetterType<Integer> TOTAL_RAIDERS_ALIVE_SETTER = SimpleDataSetter.DataSetterType.create(integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.totalRaidersAlive = integer);
    });
    public static final SimpleDataSetter.DataSetterType<Integer> GROUPS_SPAWNED_SETTER = SimpleDataSetter.DataSetterType.create(integer -> {
        RaidInfoHud.getInstance().flatMap(RaidInfoHud::geInfo).ifPresent(raidInfo -> raidInfo.groupsSpawned = integer);
    });

    public RaidInfo() {}

    public RaidInfo(int totalRaidersAlive, int groupsSpawned, int numGroups, int raidOmenLevel, int difficulty, int raidCooldownTicks) {
        this.totalRaidersAlive = totalRaidersAlive;
        this.groupsSpawned = groupsSpawned;
        this.numGroups = numGroups;
        this.raidOmenLevel = raidOmenLevel;
        this.difficulty = difficulty;
        this.raidCooldownTicks = raidCooldownTicks;
    }

    public static void encode(RaidInfo message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.totalRaidersAlive);
        buffer.writeVarInt(message.groupsSpawned);
        buffer.writeVarInt(message.numGroups);
        buffer.writeVarInt(message.raidOmenLevel);
        buffer.writeVarInt(message.difficulty);
        buffer.writeVarInt(message.raidCooldownTicks);
    }

    public static RaidInfo decode(FriendlyByteBuf buffer) {
        int totalRaidersAlive = buffer.readVarInt();
        int groupsSpawned = buffer.readVarInt();
        int numGroups = buffer.readVarInt();
        int raidOmenLevel = buffer.readVarInt();
        int difficulty = buffer.readVarInt();
        int raidCooldownTicks = buffer.readVarInt();
        return new RaidInfo(totalRaidersAlive, groupsSpawned, numGroups, raidOmenLevel, difficulty, raidCooldownTicks);
    }

    public static void handle(RaidInfo message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() == null && context.getDirection().getReceptionSide().isClient()) {
                if (net.minecraft.client.Minecraft.getInstance().player instanceof LocalPlayer) {
                    RaidInfoHud.getInstance().ifPresent(raidInfoHud -> raidInfoHud.setInfo(message));
                }
            }
        });
        context.setPacketHandled(true);
    }
}