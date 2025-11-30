package net.nebula.tmmac.mixin;

import dev.doctor4t.trainmurdermystery.util.GunShootPayload;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.nebula.tmmac.command.Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GunShootPayload.Receiver.class)
public class gunShootPayloadReceiverMixin {

    @Redirect(
            method = "receive(Ldev/doctor4t/trainmurdermystery/util/GunShootPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;distanceTo(Lnet/minecraft/entity/Entity;)F"
            )
    )
    private float replaceGunDistanceCheck(PlayerEntity victim, Entity attacker) {
        float distance = attacker.distanceTo(victim);
        distance += 65;
        distance -= Storage.getAll().get("gunrange");
        return distance;
    }

}
