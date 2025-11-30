package net.nebula.tmmac.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfigCommands {

    public static void init() {
        System.out.println("[TMMAC] Initializing commands...");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // /setGunRange <value>
            dispatcher.register(literal("setGunRange")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("gunrange", FloatArgumentType.floatArg(0))
                            .executes(context -> {
                                float gunrange = IntegerArgumentType.getInteger(context, "gunrange");
                                ServerCommandSource source = context.getSource();

                                Storage.set("gunrange", gunrange, source.getServer());
                                if (source.getEntity() instanceof ServerPlayerEntity player) {
                                    player.sendMessage(Text.literal("Gun range set to " + gunrange), true);
                                }

                                return 1;
                            })
                    )
            );

            // /setKnifeRange <value>
            dispatcher.register(literal("setKnifeRange")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("kniferange", FloatArgumentType.floatArg(0))
                            .executes(context -> {
                                int kniferange = IntegerArgumentType.getInteger(context, "kniferange");
                                ServerCommandSource source = context.getSource();

                                Storage.set("kniferange", kniferange, source.getServer());
                                if (source.getEntity() instanceof ServerPlayerEntity player) {
                                    player.sendMessage(Text.literal("Knife range set to " + kniferange), true);
                                }

                                return 1;
                            })
                    )
            );
        });

        // Load saved config on server start
        ServerLifecycleEvents.SERVER_STARTING.register(Storage::load);
    }
}
