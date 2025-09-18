package me.duncanruns.duncanstools.alignmentlocker;

import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AlignmentLocker {
    public static KeyBinding lockKeyBinding;
    public static boolean alignLock = false;
    public static boolean lockKeyWasPressed;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().alignmentLockerEnabled;
    }

    public static void scrollWithBind(boolean isUp, PlayerEntity player) {
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
        player.sendMessage(Text.translatable("duncanstools.setalignmentsplit", config.alignmentLockerSplit, 360.0 / config.alignmentLockerSplit), true);
    }

    public static void initialize() {

        // Keybindings
        lockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.togglealignmentlock",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        // Tick Event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!AlignmentLocker.moduleEnabled()) return;
            if (lockKeyBinding.isPressed() && !lockKeyWasPressed) {
                alignLock = !alignLock;
                if (alignLock) {
                    client.player.sendMessage(Text.translatable("duncanstools.alignmentlockenabled"), true);
                } else {
                    client.player.sendMessage(Text.translatable("duncanstools.alignmentlockdisabled"), true);
                }
            }
            if (alignLock) {
                if (client.player != null) {
                    float i = 360.0f / DuncansToolsConfig.getInstance().alignmentLockerSplit;
                    float newYaw = Math.round(client.player.getYaw() / i) * i;
                    client.player.setYaw(newYaw);
                } else {
                    alignLock = false;
                }
            }
            lockKeyWasPressed = lockKeyBinding.isPressed();
        });
    }

}