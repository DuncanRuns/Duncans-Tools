package me.duncanruns.duncanstools;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import me.duncanruns.duncanstools.bedrocklever.BedrockLever;
import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import me.duncanruns.duncanstools.portalcoords.PortalCoords;
import me.duncanruns.duncanstools.spamcrafting.SpamCrafting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuncansTools implements ClientModInitializer {
    public static final String MOD_ID = "duncans-tools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void ding(MinecraftClient client) {
        assert client.world != null;
        assert client.player != null;
        client.world.playSoundClient(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1f, 1f, false);
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing...");

        DuncansToolsConfig.initialize();

        BedrockLever.initialize();
        FarmClicker.initialize();
        AlignmentLocker.initialize();
        PortalCoords.initialize();
        BookTradeFinder.initialize();
//        BedrockFinder.initialize();
        SpamCrafting.initialize();

        KeyBinding configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.openconfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "duncanstools.keycategory"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            if (configKeyBinding.wasPressed()) {
                client.setScreen(DuncansToolsConfig.makeConfigScreen(client.currentScreen));
            }
        });
    }
}