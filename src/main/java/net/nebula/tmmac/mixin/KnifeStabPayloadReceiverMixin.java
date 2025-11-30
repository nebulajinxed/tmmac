package net.nebula.tmmac.mixin;

import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.util.KnifeStabPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.nebula.tmmac.command.Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KnifeStabPayload.Receiver.class)
public class KnifeStabPayloadReceiverMixin {
    @Redirect(
            method = "receive(Ldev/doctor4t/trainmurdermystery/util/KnifeStabPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;distanceTo(Lnet/minecraft/entity/Entity;)F")
    )
    private float replaceKnifeRange(PlayerEntity victim, Entity attacker) {
        float distance = attacker.distanceTo(victim);
        distance += 3;
        distance -= Storage.getAll().get("kniferange");
        return distance;
    }

    @Inject(method = "receive(Ldev/doctor4t/trainmurdermystery/util/KnifeStabPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;distanceTo(Lnet/minecraft/entity/Entity;)F",
                    shift = At.Shift.AFTER), cancellable = true)
    private void checks(KnifeStabPayload payload, ServerPlayNetworking.Context context, CallbackInfo ci) {
        ServerPlayerEntity player = context.player();
        ItemStack mainHandStack = player.getMainHandStack();

        if (!mainHandStack.isOf(TMMItems.KNIFE) && !player.hasPermissionLevel(2)) {
            ci.cancel();
            return;
        }

        if (mainHandStack.isEmpty() && !player.hasPermissionLevel(2)) {
            ci.cancel();
            return;
        }


        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR && !player.hasPermissionLevel(2)) {
            ci.cancel();
            return;
        }
        if (payload.target() == context.player().getId() && !player.hasPermissionLevel(2)) {
            ci.cancel();
        }
    }

}
