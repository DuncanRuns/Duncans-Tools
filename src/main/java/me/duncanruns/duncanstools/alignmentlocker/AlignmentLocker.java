package me.duncanruns.duncanstools.alignmentlocker;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class AlignmentLocker {
    public static KeyMapping lockKeyMapping;
    public static boolean alignLock = false;
    public static boolean lockKeyWasPressed;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().alignmentLockerEnabled;
    }

    public static void scrollWithBind(boolean isUp, Player player) {
        DuncansToolsConfig config = DuncansToolsConfig.getInstance();
        if (isUp) {
            config.alignmentLockerSplit = config.alignmentLockerSplit * 2;
        } else {
            config.alignmentLockerSplit = config.alignmentLockerSplit / 2;
        }
        if (config.alignmentLockerSplit < 1) {
            config.alignmentLockerSplit = 1;
        } else if (config.alignmentLockerSplit > 64) {
            config.alignmentLockerSplit = 64;
        }
        player.sendOverlayMessage(Component.translatable("duncanstools.setalignmentsplit", config.alignmentLockerSplit, 360.0 / config.alignmentLockerSplit));
    }

    public static void initialize() {

        // Keybindings
        lockKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.togglealignmentlock",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        // Tick Event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!AlignmentLocker.moduleEnabled()) return;
            if (lockKeyMapping.isDown() && !lockKeyWasPressed) {
                alignLock = !alignLock;
                assert client.player != null;
                if (alignLock) {
                    client.player.sendOverlayMessage(Component.translatable("duncanstools.alignmentlockenabled"));
                } else {
                    client.player.sendOverlayMessage(Component.translatable("duncanstools.alignmentlockdisabled"));
                }
            }
            if (alignLock) {
                if (client.player != null) {
                    float i = 360.0f / DuncansToolsConfig.getInstance().alignmentLockerSplit;
                    float newYaw = Math.round(client.player.getYRot() / i) * i;
                    client.player.setYRot(newYaw);
                } else {
                    alignLock = false;
                }
            }
            lockKeyWasPressed = lockKeyMapping.isDown();
        });
    }

}