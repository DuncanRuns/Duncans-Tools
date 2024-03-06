package me.duncanruns.duncanstools.booktradefinder;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;

import java.util.Objects;

public class BookTradeFinder {
    public static boolean finding = false;

    private static Identifier targetEnchantment;
    private static int minLevel;
    private static int maxEmeralds;

    private static VillagerEntity villager;
    private static String lastTradeListString = null;
    private static int tradeListRepeats = 0;

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("findbooktrade")
                    .then(ClientCommandManager.argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                            .then(ClientCommandManager.argument("minimum_level", IntegerArgumentType.integer(1, 5))
                                    .then(ClientCommandManager.argument("maximum_emeralds", IntegerArgumentType.integer(5, 64))
                                            .executes(BookTradeFinder::execute)
                                    )
                            )
                    )
            );
        });

        ClientTickEvents.END_CLIENT_TICK.register(BookTradeFinder::tick);
    }

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().bookTradeFinderEnabled;
    }

    private static void tick(MinecraftClient client) {
        if (!moduleEnabled()) {
            finding = false;
            return;
        }

        if (!finding) return;

        if (client.currentScreen == null) {
            sendFeedback(client, "Stopped trade finding because of manual cancellation.", true);
            finding = false;
            return;
        }

        if (!isTargettingCorrectVillager()) {
            sendFeedback(client, "Stopped trade finding because you are no longer looking at the villager.", true);
            finding = false;
            return;
        }

        if (!villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            sendFeedback(client, "Stopped trade finding because the villager is no longer a librarian.", true);
            finding = false;
            return;
        }

        Screen screen = client.currentScreen;
        if (!(screen instanceof MerchantScreen merchantScreen)) return;

        if (merchantScreen.getScreenHandler().getExperience() > 0) {
            sendFeedback(client, "Stopped trade finding because the villager's trades are locked.", true);
            finding = false;
            merchantScreen.close();
            return;
        }
        TradeOfferList offerList = merchantScreen.getScreenHandler().getRecipes();

        if (offerList.isEmpty()) return; // Waiting for trades to appear


        String tradeListString = offerList.toNbt().toString();
        if (Objects.equals(tradeListString, lastTradeListString)) {
            if (++tradeListRepeats >= 20) {
                sendFeedback(client, "Stopped trade finding because the villager's trades are not changing.", true);
                sendFeedback(client, "Trade finding requires a mod (such as Duncan's Tweaks) installed" + (client.isIntegratedServerRunning() ? "" : " on the server") + " which recycles trades every time you open the villager's menu.", true);
                finding = false;
                merchantScreen.close();
                return;
            }
        } else {
            tradeListRepeats = 0;
        }
        lastTradeListString = tradeListString;


        for (TradeOffer tradeOffer : offerList) {
            ItemStack book = tradeOffer.getSellItem();
            if (book.getItem() != Items.ENCHANTED_BOOK) continue;

            ItemStack emeralds = tradeOffer.getOriginalFirstBuyItem();
            if (emeralds.getCount() > maxEmeralds) continue;

            NbtCompound compound = EnchantedBookItem.getEnchantmentNbt(book).getCompound(0);
            if (EnchantmentHelper.getLevelFromNbt(compound) < minLevel) continue;
            if (!targetEnchantment.equals(EnchantmentHelper.getIdFromNbt(compound))) continue;

            sendFeedback(client, "Enchanted Book Found!", false);
            finding = false;
            DuncansTools.ding(client);
            return;
        }

        client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(merchantScreen.getScreenHandler().syncId));
        client.setScreen(new FindingTradeScreen());
        clickVillager(client);
    }

    private static void sendFeedback(MinecraftClient client, String message, boolean isError) {
        client.player.sendMessage(Text.empty().append(message).styled(style -> style.withColor(isError ? Formatting.RED : Formatting.WHITE)));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        if (!moduleEnabled()) {
            sendFeedback(context.getSource().getClient(), "The book trade finder module is not enabled in the Duncan's Tools config!", true);
            return 0;
        }

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        MinecraftClient client = context.getSource().getClient();
        if (!hitResult.getType().equals(HitResult.Type.ENTITY)) {
            context.getSource().sendError(Text.of("You are not targetting a librarian!"));
            return 0;
        }
        Entity entity = ((EntityHitResult) hitResult).getEntity();
        if (!(entity instanceof VillagerEntity)) {
            context.getSource().sendError(Text.of("You are not targetting a librarian!"));
            return 0;
        }
        villager = (VillagerEntity) entity;
        if (!villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            context.getSource().sendError(Text.of("You are not targetting a librarian!"));
            return 0;
        }
        finding = true;

        RegistryEntry.Reference<Enchantment> reference = context.getArgument("enchantment", RegistryEntry.Reference.class);
        RegistryKey<Enchantment> registryKey = reference.registryKey();
        targetEnchantment = registryKey.getValue();

        maxEmeralds = IntegerArgumentType.getInteger(context, "maximum_emeralds");
        minLevel = IntegerArgumentType.getInteger(context, "minimum_level");
        tradeListRepeats = 0;
        lastTradeListString = null;

        context.getSource().sendFeedback(Text.of("Searching for enchantment..."));
        client.setScreen(new FindingTradeScreen());
        clickVillager(client);
        return 1;
    }

    private static boolean isTargettingCorrectVillager() {
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (!hitResult.getType().equals(HitResult.Type.ENTITY)) {
            return false;
        }
        return ((EntityHitResult) hitResult).getEntity().equals(villager);
    }

    private static void clickVillager(MinecraftClient client) {
        client.interactionManager.interactEntity(client.player, villager, Hand.MAIN_HAND);
    }
}
