package me.duncanruns.duncanstools.farmclicker;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.farmclicker.mixin.MinecraftClientAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class FarmClicker {
    private static KeyBinding keyBinding;
    private static boolean afkLock = false;
    private static boolean wasPressed = false;

    private static boolean usingFreecam = false;
    private static Supplier<Boolean> checkFCMovementSupplier = null;

    private static int ticker = 0;

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

    private static void tick(MinecraftClient client) {
        if (!moduleEnabled()) return;

        if (afkLock) {
            if (client.player == null) {
                afkLock = false;
                return;
            }
            ticker++;
            while (ticker >= DuncansToolsConfig.getInstance().clickerInterval) {
                ticker = 0;
                lockClicksToo = false;
                if (DuncansToolsConfig.getInstance().clickerDoUse) {
                    try {
                        ((MinecraftClientAccess) client).invokeDoItemUse();
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        ((MinecraftClientAccess) client).setAttackCooldown(0);
                        ((MinecraftClientAccess) client).invokeDoAttack();
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

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "duncanstools.key.toggleclicker",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "duncanstools.keycategory"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!moduleEnabled()) return;

            if (keyBinding.isPressed() && !wasPressed) {
                afkLock = !afkLock;
                ticker = 0;
                if (afkLock) {
                    client.player.sendMessage(Text.translatable("duncanstools.clickerenabled"), true);
                } else {
                    client.player.sendMessage(Text.translatable("duncanstools.clickerdisabled"), true);
                }
            }
            wasPressed = keyBinding.isPressed();
        });

        // Tick Events
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tick(MinecraftClient.getInstance());
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isIntegratedServerRunning()) {
                tick(client);
            }
        });
    }
}