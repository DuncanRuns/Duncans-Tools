package me.duncanruns.duncanstools.bedrocklever;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public class BedrockLever {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().bedrockLeverEnabled;
    }

    public static void initialize() {
        KeyMapping keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.spamlever",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!moduleEnabled()) return;

            // Check player and keybind
            if (client.player == null || client.level == null || !keyMapping.isDown())
                return;
            // Check crosshair target exists and is a block
            if (client.hitResult == null || !client.hitResult.getType().equals(BlockHitResult.Type.BLOCK))
                return;
            // Check block is lever
            Block targetedBlockType = client.level.getBlockState(((BlockHitResult) client.hitResult).getBlockPos()).getBlock();

            boolean validUsage = targetedBlockType == Blocks.LEVER;
            boolean isPistonUsage = false;
            if (!validUsage && targetedBlockType == Blocks.OBSIDIAN) {
                validUsage = client.player.getMainHandItem().is(Items.PISTON) || client.player.getMainHandItem().is(Items.STICKY_PISTON);
                isPistonUsage = true;
            }
            if (!validUsage) {
                return;
            }

            if (isPistonUsage) {
                client.startUseItem();
            } else {
                assert client.gameMode != null;
                for (int i = 0; i < 64; i++) {
                    client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, (BlockHitResult) client.hitResult);
                }
            }
        });
    }
}
