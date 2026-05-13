package me.duncanruns.duncanstools.farmclicker;

import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.farmclicker.mixin.MinecraftAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class FarmClicker {
    private static KeyMapping keyMapping;
    private static boolean afkLock = false;
    private static boolean wasPressed = false;

    private static boolean usingFreecam = false;
    private static Supplier<Boolean> checkFCMovementSupplier = null;

    private static int ticker = 0;
    private static int clickerInterval = Integer.MAX_VALUE;

    private static boolean lockClicksToo = true;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().farmClickerEnabled;
    }

    public static boolean shouldPreventInteraction() {
        return moduleEnabled() && afkLock;
    }

    public static boolean shouldPreventMovement() {
        if (usingFreecam && checkFCMovementSupplier.get()) {
            return false;
        }
        return moduleEnabled() && afkLock;
    }

    public static boolean shouldPreventClickActions() {
        return moduleEnabled() && afkLock && lockClicksToo;
    }

    public static void tick(Minecraft client) {
        if (!moduleEnabled()) return;

        if (clickerInterval != DuncansToolsConfig.getInstance().clickerInterval) {
            ticker = 0;
            clickerInterval = DuncansToolsConfig.getInstance().clickerInterval;
        }

        if (afkLock) {
            if (client.player == null) {
                afkLock = false;
                return;
            }
            if (!client.hasSingleplayerServer()) ticker++;
            while (ticker >= clickerInterval) {
                ticker -= clickerInterval;
                lockClicksToo = false;
                if (DuncansToolsConfig.getInstance().clickerDoUse) {
                    try {
                        client.startUseItem();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        ((MinecraftAccess) client).setMissTime(0);
                        ((MinecraftAccess) client).invokeStartAttack();
                    } catch (Exception ignored) {
                    }
                }
                lockClicksToo = true;
            }
        }

    }

    private static void checkForFreecamMod() {
        try {
            Class<?> freecamClazz = Class.forName("net.xolt.freecam.Freecam");
            Method m1 = freecamClazz.getMethod("isEnabled");
            Method m2 = freecamClazz.getMethod("isPlayerControlEnabled");
            usingFreecam = true;
            checkFCMovementSupplier = () -> {
                try {
                    return ((Boolean) m1.invoke(null)) && !((Boolean) m2.invoke(null));
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    return false;
                }
            };
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
    }

    public static void initialize() {
        checkForFreecamMod();

        keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "duncanstools.key.toggleclicker",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                DuncansTools.KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!moduleEnabled()) return;

            if (keyMapping.consumeClick() && !wasPressed) {
                afkLock = !afkLock;
                ticker = 0;
                assert client.player != null;
                if (afkLock) {
                    client.player.sendOverlayMessage(Component.translatable("duncanstools.clickerenabled"));
                } else {
                    client.player.sendOverlayMessage(Component.translatable("duncanstools.clickerdisabled"));
                }
            }
            wasPressed = keyMapping.isDefault();
        });

        // Tick Events
        ServerTickEvents.END_SERVER_TICK.register(_ -> ticker++);
        ClientTickEvents.END_CLIENT_TICK.register(FarmClicker::tick);
    }
}