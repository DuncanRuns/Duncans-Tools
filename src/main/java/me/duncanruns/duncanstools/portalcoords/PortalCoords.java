package me.duncanruns.duncanstools.portalcoords;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.lwjgl.glfw.GLFW;

import java.util.function.UnaryOperator;

public class PortalCoords {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().portalCoordsEnabled;
    }

    public static int execute(Level world, BlockPos pos, LocalPlayer player) {
        if (!moduleEnabled()) {
            player.sendSystemMessage(Component.literal("The portal coords module is not enabled in the Duncan's Tools config!").copy().withStyle(style -> style.withColor(ChatFormatting.RED)));
            return 0;
        }

        boolean isNether;

        ResourceKey<DimensionType> key = world.dimensionTypeRegistration().unwrapKey().orElseThrow();
        if (key.equals(BuiltinDimensionTypes.NETHER)) {
            isNether = true;
        } else {
            isNether = false;
            if (!key.equals(BuiltinDimensionTypes.OVERWORLD)) {
                player.sendSystemMessage(Component.literal("Cannot get portal coordinates as you are not in the nether or the overworld!").copy().withStyle(style -> style.withColor(ChatFormatting.RED)));
                return 0;
            }
        }

        BlockPos portalPos = pos;

        for (int i = 0; i <= 1; i++) {
            BlockPos pos2 = pos.above(i);
            if (world.getBlockState(pos2).getBlock().equals(Blocks.NETHER_PORTAL)) {
                portalPos = pos2;
                break;
            } else if (world.getBlockState(pos2.north()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                portalPos = pos2.north();
                break;
            } else if (world.getBlockState(pos2.east()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                portalPos = pos2.east();
                break;
            } else if (world.getBlockState(pos2.south()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                portalPos = pos2.south();
                break;
            } else if (world.getBlockState(pos2.west()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                portalPos = pos2.west();
                break;
            }
        }

        UnaryOperator<Integer> conversionType = isNether ? (i -> i * 8) : (i -> i / 8);

        BlockPos otherPortalPos = new BlockPos(
                conversionType.apply(portalPos.getX()),
                portalPos.getY(),
                conversionType.apply(portalPos.getZ())
        );

        MutableComponent thisDimension, otherDimension;
        if (isNether) {
            thisDimension = Component.translatable("advancements.nether.root.title");
            otherDimension = Component.translatable("flat_world_preset.minecraft.overworld");
        } else {
            thisDimension = Component.translatable("flat_world_preset.minecraft.overworld");
            otherDimension = Component.translatable("advancements.nether.root.title");
        }


        MutableComponent text = Component.literal("").append(thisDimension.append(String.format(": %d %d %d", portalPos.getX(), portalPos.getY(), portalPos.getZ())).withStyle(style -> style.withColor(ChatFormatting.GRAY)))
                .append(Component.literal("\n-> ").withStyle(style -> style.withColor(ChatFormatting.DARK_PURPLE))).append(otherDimension.append(String.format(": %d %d %d", otherPortalPos.getX(), otherPortalPos.getY(), otherPortalPos.getZ())).withStyle(style -> style.withColor(isNether ? ChatFormatting.GREEN : ChatFormatting.RED)));

        player.sendSystemMessage(text);

        return 1;
    }

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) ->
                dispatcher.register(ClientCommands.literal("portal").executes(context -> PortalCoords.execute(context.getSource().getLevel(), BlockPos.containing(context.getSource().getPosition()), context.getSource().getPlayer())))
        );

        final KeyMapping keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.portalcoords",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        ClientTickEvents.END_LEVEL_TICK.register(world -> {
            if (keyMapping.consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) {
                    return;
                }
                PortalCoords.execute(world, player.blockPosition(), player);
            }
        });
    }
}