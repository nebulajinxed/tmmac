package net.nebula.tmmac.checks;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class DropCheck {

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(DropCheck::checkDrops);

    }

    private static void checkDrops(MinecraftServer server) {
        Box worldBox = new Box(-1e6, -1e6, -1e6, 1e6, 1e6, 1e6);

        for (World world : server.getWorlds()) {
            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, worldBox, e -> true)) {

                Entity owner = itemEntity.getOwner();
                if (owner instanceof PlayerEntity playerOwner) {
                    if (playerOwner instanceof ServerPlayerEntity serverPlayer &&
                            (serverPlayer.interactionManager.getGameMode() == GameMode.CREATIVE ||
                                    serverPlayer.interactionManager.getGameMode() == GameMode.SPECTATOR)) {
                        continue;
                    }


                    ItemStack stack = itemEntity.getStack();
                    if (!stack.isEmpty()) {
                        int selectedSlot = playerOwner.getInventory().selectedSlot;
                        ItemStack currentStack = playerOwner.getInventory().getStack(selectedSlot);
                        if (!playerOwner.hasPermissionLevel(2)) {
                            if (currentStack.isEmpty()) {
                                playerOwner.getInventory().setStack(selectedSlot, stack);
                            } else if (currentStack.isOf(stack.getItem())) {
                                int maxCount = currentStack.getMaxCount();
                                int newCount = Math.min(currentStack.getCount() + stack.getCount(), maxCount);
                                currentStack.setCount(newCount);
                            } else {
                                playerOwner.getInventory().insertStack(playerOwner.getInventory().getEmptySlot(), stack);
                            }

                            itemEntity.remove(RemovalReason.DISCARDED);
                        }
                    }

                }
            }
        }
    }

}
