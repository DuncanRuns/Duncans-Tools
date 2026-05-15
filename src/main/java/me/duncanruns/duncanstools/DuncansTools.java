package me.duncanruns.duncanstools;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import me.duncanruns.duncanstools.bedrocklever.BedrockLever;
import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import me.duncanruns.duncanstools.portalcoords.PortalCoords;
import me.duncanruns.duncanstools.spamcrafting.SpamCrafting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuncansTools implements ClientModInitializer {
    public static final String MOD_ID = "duncans-tools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "keys"));

    public static void ding(Minecraft client) {
        assert client.level != null;
        assert client.player != null;
        client.level.playLocalSound(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1f, 1f, false);
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

        KeyMapping configKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.openconfig",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            if (configKeyMapping.consumeClick()) {
                client.setScreen(DuncansToolsConfig.makeConfigScreen(client.screen));
            }
        });
    }
}