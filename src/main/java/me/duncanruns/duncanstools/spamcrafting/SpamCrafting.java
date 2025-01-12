package me.duncanruns.duncanstools.spamcrafting;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SpamCrafting {
    public static KeyBinding keyBinding;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().spamCraftingEnabled;
    }

    public static void initialize() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.spamcraft",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "duncanstools.keycategory"
        ));
    }
}
