package net.nebula.tmmac.checks;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JumpCheck {

    private static final Map<ServerPlayerEntity, Double> prevY = new HashMap<>();
    private static final Map<ServerPlayerEntity, Boolean> prevOnGround = new HashMap<>();
    private static final Map<ServerPlayerEntity, Double> prevVelY = new HashMap<>();

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.interactionManager.getGameMode() == GameMode.CREATIVE || player.interactionManager.getGameMode() == GameMode.SPECTATOR) continue;

            double lastY = prevY.getOrDefault(player, player.getY());
            boolean lastOnGround = prevOnGround.getOrDefault(player, player.isOnGround());
            double lastVelY = prevVelY.getOrDefault(player, 0.0);

            double currentVelY = player.getY() - lastY; // approximate vertical velocity per tick

            if (isJump(player, lastVelY, currentVelY, lastOnGround)) {
                onPlayerJump(player);
            }

            prevY.put(player, player.getY());
            prevOnGround.put(player, player.isOnGround());
            prevVelY.put(player, currentVelY);
        }
    }

    private static boolean isJump(ServerPlayerEntity player, double lastVelY, double currentVelY, boolean lastOnGround) {
        if (!lastOnGround) return false;

        if (lastVelY > 0) return false;

        if (player.isClimbing() || player.isSwimming() || player.hasVehicle() || player.hasStatusEffect(StatusEffects.LEVITATION))
            return false;

        double jumpBoost = player.hasStatusEffect(StatusEffects.JUMP_BOOST)
                ? 0.1 * (Objects.requireNonNull(player.getStatusEffect(StatusEffects.JUMP_BOOST)).getAmplifier() + 1)
                : 0.0;
        double expectedJumpVelocity = 0.41999998688697815 + jumpBoost;

        if (currentVelY > expectedJumpVelocity + 0.01) {
            return isSlimeBelow(player);
        }

        return Math.abs(currentVelY - expectedJumpVelocity) < 0.01;
    }

    private static boolean isSlimeBelow(ServerPlayerEntity player) {
        BlockPos posBelow = player.getBlockPos().down();
        return player.getWorld().getBlockState(posBelow).isOf(Blocks.SLIME_BLOCK);
    }

    private static void onPlayerJump(ServerPlayerEntity player) {
        double prevYPosition = prevY.getOrDefault(player, player.getY());
        double prevX = player.getX();
        double prevZ = player.getZ();
        if (!player.hasPermissionLevel(2)) {
            player.teleport(prevX, prevYPosition, prevZ, false);
            player.setVelocity(0, 0, 0);
        }
        prevVelY.put(player, 0.0d);
    }
}
