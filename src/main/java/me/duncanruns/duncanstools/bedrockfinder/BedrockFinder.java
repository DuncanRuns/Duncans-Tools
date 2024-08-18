package me.duncanruns.duncanstools.bedrockfinder;

import com.mojang.brigadier.context.CommandContext;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Objects;

public class BedrockFinder {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().bedrockFinderEnabled;
    }

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("findbedrock").executes(BedrockFinder::execute))
        );
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        if (!moduleEnabled()) {
            context.getSource().sendError(Text.literal("Module is not enabled! Cheater!!!"));
            return 0;
        }

        ClientWorld world = context.getSource().getWorld();

        BlockPos.Mutable b = context.getSource().getPlayer().getBlockPos().mutableCopy().setY(124);
        Direction direction = Direction.EAST;


        if (!world.getBlockState(b.add(0, 3, 0)).getBlock().equals(Blocks.BEDROCK)) {
            context.getSource().sendError(Text.literal("This dimension does not have a nether roof!"));
        }

        for (int linesSearched = 0; linesSearched < 20000; linesSearched++) {

            ChunkPos chunkPos = new ChunkPos(b);
            if (!world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
                break;
            }

            int currentLineLength = linesSearched >> 1;
            for (int i = 0; i <= currentLineLength; i++) {
                boolean found = true;
                for (BlockPos blockPos : Arrays.asList(
                        b.add(0, 0, 0),
                        b.add(1, 0, 0),
                        b.add(2, 0, 0),
                        b.add(0, 0, 1),
                        b.add(1, 0, 1),
                        b.add(2, 0, 1),
                        b.add(0, 0, 2),
                        b.add(1, 0, 2),
                        b.add(2, 0, 2),

                        b.add(0, 1, 0),
                        b.add(1, 1, 0),
                        b.add(2, 1, 0),
                        b.add(0, 1, 1),
                        // mutable.add(1, 1, 1),
                        b.add(2, 1, 1),
                        b.add(0, 1, 2),
                        b.add(1, 1, 2),
                        b.add(2, 1, 2)

                        // b.add(0, 2, 0),
                        // b.add(1, 2, 0),
                        // b.add(2, 2, 0),
                        // b.add(0, 2, 1),
                        // mutable.add(1, 2, 1),
                        // b.add(2, 2, 1),
                        // b.add(0, 2, 2),
                        // b.add(1, 2, 2),
                        // b.add(2, 2, 2)
                )) {
                    if (!Objects.equals(world.getBlockState(blockPos).getBlock(), Blocks.BEDROCK)) {
                        found = false;
                        break;
                    }
                }
                if (found && !Objects.equals(world.getBlockState(b.add(1, 2, 1)).getBlock(), Blocks.BEDROCK)) {
                    context.getSource().sendFeedback(Text.literal(String.format("Found at %d %d", b.getX(), b.getZ())));
                    return 1;
                }
                b.move(direction);
            }
            direction = direction.rotateYClockwise();
        }
        context.getSource().sendError(Text.literal("Couldn't find any matching bedrock formations!"));
        return 0;
    }

//    private static boolean hasBedrockFormation(BedrockRoofChecker roofChecker, BlockPos.Mutable b) {
//        BlockPos.Mutable mutable = b.mutableCopy();
//        for (BlockPos blockPos : Arrays.asList(
//                mutable.add(0, 0, 0),
//                mutable.add(1, 0, 0),
//                mutable.add(2, 0, 0),
//                mutable.add(0, 0, 1),
//                mutable.add(1, 0, 1),
//                mutable.add(2, 0, 1),
//                mutable.add(0, 0, 2),
//                mutable.add(1, 0, 2),
//                mutable.add(2, 0, 2),
//
//                mutable.add(0, 1, 0),
//                mutable.add(1, 1, 0),
//                mutable.add(2, 1, 0),
//                mutable.add(0, 1, 1),
//                // mutable.add(1, 1, 1),
//                mutable.add(2, 1, 1),
//                mutable.add(0, 1, 2),
//                mutable.add(1, 1, 2),
//                mutable.add(2, 1, 2),
//
//                mutable.add(0, 2, 0),
//                mutable.add(1, 2, 0),
//                mutable.add(2, 2, 0),
//                mutable.add(0, 2, 1),
//                // mutable.add(1, 2, 1),
//                mutable.add(2, 2, 1),
//                mutable.add(0, 2, 2),
//                mutable.add(1, 2, 2),
//                mutable.add(2, 2, 2)
//        )) {
//            if (!roofChecker.isBedrock(blockPos)) {
//                return false;
//            }
//        }
//        return !roofChecker.isBedrock(b.add(1, 2, 1));
//    }

    public static double lerpFromProgress(double lerpValue, double lerpStart, double lerpEnd, double start, double end) {
        return MathHelper.lerp(MathHelper.getLerpProgress(lerpValue, lerpStart, lerpEnd), start, end);
    }

//    static class BedrockRoofChecker {
//        final RandomSplitter r;
//
//        BedrockRoofChecker(long worldSeed) {
//            r = ChunkRandom.RandomProvider.XOROSHIRO.create(worldSeed).nextSplitter().split("minecraft:bedrock_roof").nextSplitter();
//        }
//
//        boolean isBedrock(BlockPos b) {
//            return (double) r.split(b).nextFloat() < lerpFromProgress(b.getY(), 128 - 5, 128, 1.0, 0.0);
//        }
//    }
}