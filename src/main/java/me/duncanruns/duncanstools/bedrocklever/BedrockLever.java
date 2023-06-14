package me.duncanruns.duncanstools.bedrocklever;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class BedrockLever {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().bedrockLeverEnabled;
    }

    public static void initialize() {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.spamlever",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "duncanstools.keycategory"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!moduleEnabled()) return;

            // Check player and keybind
            if (client.player == null || client.world == null || !keyBinding.isPressed())
                return;
            // Check crosshair target exists and is a block
            if (client.crosshairTarget == null || !client.crosshairTarget.getType().equals(HitResult.Type.BLOCK))
                return;
            // Check block is lever
            if (!client.world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).getBlock().equals(Blocks.LEVER))
                return;

            for (int i = 0; i < 64; i++) {
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, (BlockHitResult) client.crosshairTarget);
            }
            client.player.swingHand(Hand.MAIN_HAND);
        });
    }
}
