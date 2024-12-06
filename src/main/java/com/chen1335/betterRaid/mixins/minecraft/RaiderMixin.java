package com.chen1335.betterRaid.mixins.minecraft;

import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Raider.class)
public class RaiderMixin {
    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;joinRaid(ILnet/minecraft/world/entity/raid/Raider;Lnet/minecraft/core/BlockPos;Z)V"))
    private int joinRaid(int wave) {
        return 0;
    }
}
