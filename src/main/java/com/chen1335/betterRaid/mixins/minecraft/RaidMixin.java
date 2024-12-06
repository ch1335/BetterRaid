package com.chen1335.betterRaid.mixins.minecraft;

import com.chen1335.betterRaid.mixinsAPI.minecraft.RaidModifier;
import com.chen1335.betterRaid.network.RaidMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(Raid.class)
public abstract class RaidMixin {

    @Shadow
    @Final
    public ServerBossEvent raidEvent;

    @Shadow
    @Final
    public Map<Integer, Set<Raider>> groupRaiderMap;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        nsram$raidModifier.tick((Raid) (Object) this);
    }

    @Unique
    private RaidModifier nsram$raidModifier;


    @Inject(method = "<init>(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"))
    private void init(int id, ServerLevel level, BlockPos center, CallbackInfo ci) {
        nsram$raidModifier = new RaidModifier(id, level, center);
    }

    @ModifyArg(method = "updatePlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer addPlayer(ServerPlayer player) {
        nsram$raidModifier.sendDataToClient(player, (Raid) (Object) this);
        return player;
    }

    @ModifyArg(method = "updatePlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;removePlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerPlayer removePlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new RaidMessage(0));
        return player;
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci) {
        raidEvent.getPlayers().forEach(serverPlayer -> {
            PacketDistributor.sendToPlayer(serverPlayer, new RaidMessage(0));
        });
    }

    @Inject(method = "spawnGroup", at = @At("HEAD"))
    private void spawnGroupStart(BlockPos pos, CallbackInfo ci) {
        nsram$raidModifier.spawnGroupStart((Raid) (Object) this);
    }

    @Inject(method = "spawnGroup", at = @At("RETURN"))
    private void spawnGroupFinish(BlockPos pos, CallbackInfo ci) {
        nsram$raidModifier.spawnGroupFinish((Raid) (Object) this);
    }

    @Inject(method = "getTotalRaidersAlive", at = @At("RETURN"), cancellable = true)
    private void getTotalRaidersAlive(CallbackInfoReturnable<Integer> cir) {
        if (groupRaiderMap.containsKey(0)) {
            cir.setReturnValue(cir.getReturnValue() - groupRaiderMap.get(0).size());
        }
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void save(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        nsram$raidModifier.save(compound);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void load(ServerLevel level, CompoundTag compound, CallbackInfo ci) {
        nsram$raidModifier = new RaidModifier(level, compound);
    }
}
