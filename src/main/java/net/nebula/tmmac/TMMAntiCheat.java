package net.nebula.tmmac;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.nebula.tmmac.checks.DropCheck;
import net.nebula.tmmac.checks.JumpCheck;
import net.nebula.tmmac.command.ConfigCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMMAntiCheat implements ModInitializer {
	public static final String MOD_ID = "trainmurdermysteryserversidechecks";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ConfigCommands.init();

        ServerTickEvents.END_SERVER_TICK.register(JumpCheck::onServerTick);
        DropCheck.init();
	}
}