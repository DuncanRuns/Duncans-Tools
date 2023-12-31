package me.duncanruns.duncanstools.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import me.duncanruns.duncanstools.DuncansTools;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class DuncansToolsConfig implements ModMenuApi {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(DuncansTools.MOD_ID + ".json");
    private static final GsonConfigInstance<DuncansToolsConfig> CONFIG_INSTANCE = GsonConfigInstance.createBuilder(DuncansToolsConfig.class).setPath(CONFIG_PATH).build();

    @ConfigEntry
    public boolean alignmentLockerEnabled = false;
    @ConfigEntry
    public boolean bedrockLeverEnabled = false;
    @ConfigEntry
    public boolean bookTradeFinderEnabled = false;
    @ConfigEntry
    public boolean craftRefillEnabled = false;
    @ConfigEntry
    public boolean farmClickerEnabled = false;
    @ConfigEntry
    public boolean gammaOverrideEnabled = false;
    @ConfigEntry
    public boolean librarianBookHelperEnabled = false;
    @ConfigEntry
    public boolean portalCoordsEnabled = false;

    @ConfigEntry
    public int clickerInterval = 30;
    @ConfigEntry
    public boolean clickerDoUse = false;
    @ConfigEntry
    public int alignmentLockerSplit = 8;
    @ConfigEntry
    public double brightGamma = 5d;
    @ConfigEntry
    public String librarianHighlight = "";
    @ConfigEntry
    public boolean librarianHighlightDing = true;

    public static void initialize() {
        CONFIG_INSTANCE.load();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            CONFIG_INSTANCE.save();
        });
    }

    public static Screen makeConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Duncan's Tools Config Page"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Duncan's Tools Config"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Modules"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Alignment Locker"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the alignment locker module, which locks the player's yaw rotation to a set of angles."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().alignmentLockerEnabled, () -> getInstance().alignmentLockerEnabled, val -> getInstance().alignmentLockerEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Bedrock Lever"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the bedrock lever module, which allows the player to spam a lever 64 times per tick, which lags the server-side enough to allow bedrock breaking."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().bedrockLeverEnabled, () -> getInstance().bedrockLeverEnabled, val -> getInstance().bedrockLeverEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Book Trade Finder"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the trade finder module, which allows the usage of /findbooktrade which re-opens a new librarian's trade menu until the correct trades are found. Requires a mod which re-rolls the trades every time the menu of a new librarian is opened (such as Duncan's Tweaks)."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().bookTradeFinderEnabled, () -> getInstance().bookTradeFinderEnabled, val -> getInstance().bookTradeFinderEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Craft Refill"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the craft refill module, which allows the player to refill a crafting table or stonecutter with the last selected recipe by pressing shift+space, similar to villager refilling. Also allows the usage of ctrl+shift+space to spam craft (works best with carpet's ctrlQCraftingFix)."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().craftRefillEnabled, () -> getInstance().craftRefillEnabled, val -> getInstance().craftRefillEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Farm Clicker"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the alignment locker module, which locks the player's yaw rotation to a set of angles."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().farmClickerEnabled, () -> getInstance().farmClickerEnabled, val -> getInstance().farmClickerEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Gamma Override"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the gamma override module, which overrides the gamma level of vanilla's \"bright\" setting."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().gammaOverrideEnabled, () -> getInstance().gammaOverrideEnabled, val -> getInstance().gammaOverrideEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Librarian Book Helper"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the librarian book helper module, which overlays book enchantments in the villager trading GUI without having to hover over the book. It also lets you set a highlighted enchantment ID which causes a ding sound to be played when the correct enchantment appears, and will highlight the enchantment in bold green."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().librarianBookHelperEnabled, () -> getInstance().librarianBookHelperEnabled, val -> getInstance().librarianBookHelperEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Portal Coords"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Enables the portal coords module, which allows the usage of the /portal command and the relevant keybind which converts the player's coordinates to the other dimension's coordinates."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().portalCoordsEnabled, () -> getInstance().portalCoordsEnabled, val -> getInstance().portalCoordsEnabled = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Farm Clicker"))
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Interval"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Sets the interval between clicks, measured in ticks. 20 ticks = 1 second."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().clickerInterval, () -> getInstance().clickerInterval, val -> getInstance().clickerInterval = val)
                                        .controller(integerOption -> IntegerFieldControllerBuilder.create(integerOption).min(1))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Do Use Instead (Right Click)"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Uses the item in hand or interacts with the targeted block as if you were right clicking instead of attacking/mining."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().clickerDoUse, () -> getInstance().clickerDoUse, val -> getInstance().clickerDoUse = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter())
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Gamma Allower"))
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Bright Gamma"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Specifies the actual gamma wanted when the in-game gamma is set to \"Bright\"."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().brightGamma, () -> getInstance().brightGamma, val -> getInstance().brightGamma = val)
                                        .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(1d, 20d).step(0.1d))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Librarian Book Helper"))
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Highlighted Enchantment ID"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("The enchantments with a matching ID will be highlighted in green and a sound will play when opening a trade GUI that contains a book with the matching ID. You can also specify a minimum level after a space.\n" +
                                                        "\n" +
                                                        "Examples:\n" +
                                                        "minecraft:mending\n" +
                                                        "fire_protection 3\n" +
                                                        "minecraft:frost_walker 2"))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().librarianHighlight, () -> CONFIG_INSTANCE.getConfig().librarianHighlight, val -> getInstance().librarianHighlight = val)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Highlighted Enchantment Ding"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.of("Will make a \"ding\" sound when opening the villager trading GUI if the offers include a book with the highlighted enchantment."))
                                                .build())
                                        .binding(CONFIG_INSTANCE.getDefaults().librarianHighlightDing, () -> CONFIG_INSTANCE.getConfig().librarianHighlightDing, val -> getInstance().librarianHighlightDing = val)
                                        .controller(opt -> BooleanControllerBuilder.create(opt).onOffFormatter())
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }

    public static DuncansToolsConfig getInstance() {
        return CONFIG_INSTANCE.getConfig();
    }

    public String getLibrarianHighlight() {
        String[] strings = librarianHighlight.split(" ");
        if (strings.length < 1) {
            return "";
        }
        return strings[0];
    }

    public int getLibrarianHighlightMinLevel() {
        String[] strings = librarianHighlight.split(" ");
        if (strings.length < 2) {
            return 1;
        }
        try {
            return Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
            librarianHighlight = librarianHighlight.split(" ")[0];
            return 1;
        }
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DuncansToolsConfig::makeConfigScreen;
    }
}
