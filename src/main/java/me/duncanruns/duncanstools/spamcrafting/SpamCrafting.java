package me.duncanruns.duncanstools.spamcrafting;

import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class SpamCrafting {
    public static KeyMapping keyMapping;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().spamCraftingEnabled;
    }

    public static void initialize() {
        keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.spamcraft",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));
    }
}
