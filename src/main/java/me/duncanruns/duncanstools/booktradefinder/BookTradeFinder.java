package me.duncanruns.duncanstools.booktradefinder;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.librarianbookhelper.LibrarianBookHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.Objects;

public class BookTradeFinder {
    public static boolean finding = false;

    private static Identifier targetEnchantment;
    private static int minLevel;
    private static int maxEmeralds;

    private static Villager villager;
    private static Object lastTradeListString = null;
    private static int tradeListRepeats = 0;

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommands.literal("findbooktrade")
                .then(ClientCommands.argument("enchantment", ResourceArgument.resource(registryAccess, Registries.ENCHANTMENT))
                        .then(ClientCommands.argument("minimum_level", IntegerArgumentType.integer(1, 5))
                                .then(ClientCommands.argument("maximum_emeralds", IntegerArgumentType.integer(5, 64))
                                        .executes(BookTradeFinder::execute)
                                )
                        )
                )
        ));

        ClientTickEvents.END_CLIENT_TICK.register(BookTradeFinder::tick);
    }

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().bookTradeFinderEnabled;
    }

    private static void tick(Minecraft client) {
        if (!moduleEnabled()) {
            finding = false;
            return;
        }

        if (!finding) return;

        if (client.screen == null) {
            sendFeedback(client, "Stopped trade finding because of manual cancellation.", true);
            finding = false;
            return;
        }

        if (!isTargettingCorrectVillager()) {
            sendFeedback(client, "Stopped trade finding because you are no longer looking at the villager.", true);
            finding = false;
            return;
        }

        if (!Objects.equals(villager.getVillagerData().profession().unwrapKey().orElse(null), (VillagerProfession.LIBRARIAN))) {
            sendFeedback(client, "Stopped trade finding because the villager is no longer a librarian.", true);
            finding = false;
            return;
        }

        Screen screen = client.screen;
        if (!(screen instanceof MerchantScreen merchantScreen)) return;

        if (merchantScreen.getMenu().getTraderXp() > 0) {
            sendFeedback(client, "Stopped trade finding because the villager's trades are locked.", true);
            finding = false;
            merchantScreen.onClose();
            return;
        }
        MerchantOffers offerList = merchantScreen.getMenu().getOffers();

        if (offerList.isEmpty()) return; // Waiting for trades to appear


        Object tradeListString = offerList.stream().map(tradeOffer -> Arrays.asList(tradeOffer.getItemCostA().itemStack().getItem(), tradeOffer.getItemCostB().map(i -> i.itemStack().getItem()).orElse(null), tradeOffer.getResult().getItem())).toList();
        if (Objects.equals(tradeListString, lastTradeListString)) {
            if (++tradeListRepeats >= 20) {
                sendFeedback(client, "Stopped trade finding because the villager's trades are not changing.", true);
                sendFeedback(client, "Trade finding requires a mod (such as Duncan's Tweaks) installed" + (client.hasSingleplayerServer() ? "" : " on the server") + " which recycles trades every time you open the villager's menu.", true);
                finding = false;
                merchantScreen.onClose();
                return;
            }
        } else {
            tradeListRepeats = 0;
        }
        lastTradeListString = tradeListString;


        for (MerchantOffer tradeOffer : offerList) {
            ItemStack book = tradeOffer.getResult();
            if (book.getItem() != Items.ENCHANTED_BOOK) continue;

            ItemStack emeralds = tradeOffer.getCostA();
            if (emeralds.getCount() > maxEmeralds) continue;

            Holder<Enchantment> entry = EnchantmentHelper.getEnchantmentsForCrafting(book).keySet().stream().findFirst().orElse(null);
            if (entry == null) continue;

            if (LibrarianBookHelper.getBookLevel(entry, book) < minLevel) continue;
            if (!targetEnchantment.equals(entry.unwrapKey().map(ResourceKey::identifier).orElse(null))) continue;

            sendFeedback(client, "Enchanted Book Found!", false);
            finding = false;
            DuncansTools.ding(client);
            return;
        }

        Objects.requireNonNull(client.getConnection()).send(new ServerboundContainerClosePacket(merchantScreen.getMenu().containerId));
        clickVillager(client);
    }

    private static void sendFeedback(Minecraft client, String message, boolean isError) {
        assert client.player != null;
        client.player.sendSystemMessage(Component.empty().append(message).withStyle(style -> style.withColor(isError ? ChatFormatting.RED : ChatFormatting.WHITE)));
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        if (!moduleEnabled()) {
            sendFeedback(context.getSource().getClient(), "The book trade finder module is not enabled in the Duncan's Tools config!", true);
            return 0;
        }

        HitResult hitResult = Minecraft.getInstance().hitResult;
        Minecraft client = context.getSource().getClient();
        if (hitResult == null || !hitResult.getType().equals(EntityHitResult.Type.ENTITY)) {
            context.getSource().sendError(Component.literal("You are not targetting a librarian!"));
            return 0;
        }
        Entity entity = ((EntityHitResult) hitResult).getEntity();
        if (!(entity instanceof Villager)) {
            context.getSource().sendError(Component.literal("You are not targetting a librarian!"));
            return 0;
        }
        villager = (Villager) entity;
        if (!Objects.equals(villager.getVillagerData().profession().unwrapKey().orElse(null), (VillagerProfession.LIBRARIAN))) {
            context.getSource().sendError(Component.literal("You are not targetting a librarian!"));
            return 0;
        }
        finding = true;

        Holder.Reference<Enchantment> reference = getEnchantmentRegistryEntryReference(context);
        ResourceKey<Enchantment> registryKey = reference.key();
        targetEnchantment = registryKey.identifier();

        maxEmeralds = IntegerArgumentType.getInteger(context, "maximum_emeralds");
        minLevel = IntegerArgumentType.getInteger(context, "minimum_level");
        tradeListRepeats = 0;
        lastTradeListString = null;

        context.getSource().sendFeedback(Component.literal("Searching for enchantment..."));
        clickVillager(client);
        return 1;
    }

    @SuppressWarnings("unchecked")
    private static Holder.Reference<Enchantment> getEnchantmentRegistryEntryReference(CommandContext<FabricClientCommandSource> context) {
        return context.getArgument("enchantment", Holder.Reference.class);
    }

    private static boolean isTargettingCorrectVillager() {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        assert hitResult != null;
        if (!hitResult.getType().equals(EntityHitResult.Type.ENTITY)) {
            return false;
        }
        return ((EntityHitResult) hitResult).getEntity().equals(villager);
    }

    private static void clickVillager(Minecraft client) {
        assert client.gameMode != null;
        assert client.player != null;
        client.gameMode.interact(client.player, villager, (EntityHitResult) client.hitResult, InteractionHand.MAIN_HAND);
    }
}
