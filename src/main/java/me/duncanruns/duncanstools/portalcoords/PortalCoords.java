package me.duncanruns.duncanstools.portalcoords;

import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.lwjgl.glfw.GLFW;

import java.util.function.UnaryOperator;

public class PortalCoords {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().portalCoordsEnabled;
    }

    public static int execute(World world, BlockPos pos, PlayerEntity player) {
        if (!moduleEnabled()) {
            player.sendMessage(Text.of("The portal coords module is not enabled in the Duncan's Tools config!").copy().styled(style -> style.withColor(Formatting.RED)),false);
            return 0;
        }

        boolean isNether;

        RegistryKey<DimensionType> key = world.getDimensionEntry().getKey().get();
        if (key.equals(DimensionTypes.THE_NETHER)) {
            isNether = true;
        } else {
            isNether = false;
            if (!key.equals(DimensionTypes.OVERWORLD)) {
                player.sendMessage(Text.of("Cannot get portal coordinates as you are not in the nether or the overworld!").copy().styled(style -> style.withColor(Formatting.RED)),false);
                return 0;
            }
        }

        BlockPos portalPos = pos;

        for (int i = 0; i <= 1; i++) {
            BlockPos pos2 = pos.up(i);
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

        MutableText thisDimension, otherDimension;
        if (isNether) {
            thisDimension = Text.translatable("advancements.nether.root.title");
            otherDimension = Text.translatable("flat_world_preset.minecraft.overworld");
        } else {
            thisDimension = Text.translatable("flat_world_preset.minecraft.overworld");
            otherDimension = Text.translatable("advancements.nether.root.title");
        }


        MutableText text = Text.literal("").append(thisDimension.append(String.format(": %d %d %d", portalPos.getX(), portalPos.getY(), portalPos.getZ())).styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("\n-> ").styled(style -> style.withColor(Formatting.DARK_PURPLE))).append(otherDimension.append(String.format(": %d %d %d", otherPortalPos.getX(), otherPortalPos.getY(), otherPortalPos.getZ())).styled(style -> style.withColor(isNether ? Formatting.GREEN : Formatting.RED)));

        player.sendMessage(text,false);

        return 1;
    }

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("portal").executes(context -> PortalCoords.execute(context.getSource().getWorld(), BlockPos.ofFloored(context.getSource().getPosition()), context.getSource().getPlayer())))
        );

        final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.portalcoords",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (keyBinding.wasPressed()) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) {
                    return;
                }
                PortalCoords.execute(world, player.getBlockPos(), player);
            }
        });
    }
}