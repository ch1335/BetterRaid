package com.chen1335.betterRaid.mixinsAPI.minecraft;

import com.chen1335.betterRaid.Config;
import com.chen1335.betterRaid.network.RaidInfo;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class RaidModifier {

    public int autoNextWaveTineLeft = Config.autoEnterNextWaveTick;

    public int oldTotalRaidersAlive = 0;

    public Difficulty difficulty;

    public RaidModifier(int id, ServerLevel level, BlockPos center) {
        difficulty = level.getDifficulty();
    }

    public RaidModifier(ServerLevel level, CompoundTag compound) {
        difficulty = level.getDifficulty();
    }

    public void save(CompoundTag compound) {
    }

    public void tick(Raid raid) {
        if (!raid.isBetweenWaves() && raid.getGroupsSpawned() != 0 && Config.rideAutoNextWave && !raid.raidEvent.getPlayers().isEmpty()) {
            autoNextWaveTineLeft--;
        }

        if (raid.getTotalRaidersAlive() != oldTotalRaidersAlive) {
            raid.raidEvent.getPlayers().forEach(serverPlayer -> {
                RaidInfo.TOTAL_RAIDERS_ALIVE_SETTER.send(serverPlayer, raid.getTotalRaidersAlive());
            });
        }
        oldTotalRaidersAlive = raid.getTotalRaidersAlive();
        if (raid.ticksActive % 20 == 0) {
            raid.raidEvent.getPlayers().forEach(serverPlayer -> {
                if (raid.isBetweenWaves() || raid.getGroupsSpawned() == 0 || !Config.rideAutoNextWave) {
                    RaidInfo.RAID_COOLDOWN_TICKS_SETTER.send(serverPlayer, raid.raidCooldownTicks);
                } else {
                    RaidInfo.RAID_COOLDOWN_TICKS_SETTER.send(serverPlayer, autoNextWaveTineLeft);
                }
            });
        }

        if (autoNextWaveTineLeft <= 0 && raid.getTotalRaidersAlive() != 0 && Config.rideAutoNextWave) {
            raid.groupRaiderMap.get(raid.getGroupsSpawned()).forEach(raider -> {
                raid.groupRaiderMap.computeIfAbsent(0, integer -> Sets.newHashSet());
                raider.setWave(0);
                raid.groupRaiderMap.get(0).add(raider);
            });
            raid.groupRaiderMap.get(raid.getGroupsSpawned()).clear();
            autoNextWaveTineLeft = Config.autoEnterNextWaveTick;
            raid.raidCooldownTicks = 300;
        }
    }

    public void sendDataToClient(Raid raid) {
        if (difficulty == null) {
            difficulty = raid.getLevel().getDifficulty();
        }
        raid.raidEvent.getPlayers().forEach(serverPlayer -> {
            sendDataToClient(serverPlayer, raid);
        });
    }

    public void sendDataToClient(ServerPlayer serverPlayer, Raid raid) {
        if (difficulty == null) {
            difficulty = raid.getLevel().getDifficulty();
        }
        PacketDistributor.sendToPlayer(serverPlayer, new RaidInfo(raid.getTotalRaidersAlive(), raid.getGroupsSpawned(), raid.numGroups, raid.getRaidOmenLevel(), difficulty.getId(), raid.raidCooldownTicks));
    }

    public void spawnGroupFinish(Raid raid) {
        Map<EntityType<?>, Integer> map = new HashMap<>();
        for (Raider raider : raid.groupRaiderMap.get(raid.getGroupsSpawned())) {
            if (map.containsKey(raider.getType())) {
                map.put(raider.getType(), map.get(raider.getType()) + 1);
            } else {
                map.put(raider.getType(), 1);
            }
        }

        MutableComponent waveInfo = Component.translatable("better_raid.waveInfo.currentWave", raid.getGroupsSpawned());

        map.forEach((entityType, count) -> {
            waveInfo.append(" ").append(entityType.getDescription()).append("x").append(String.valueOf(count));
        });

        raid.raidEvent.getPlayers().forEach(serverPlayer -> {
            serverPlayer.sendSystemMessage(waveInfo);
        });

        autoNextWaveTineLeft = 3600;
        raid.raidEvent.getPlayers().forEach(serverPlayer -> {
            RaidInfo.GROUPS_SPAWNED_SETTER.send(serverPlayer, raid.getGroupsSpawned());
        });
    }

    public void spawnGroupStart(Raid raid) {
    }
}
