package me.duncanruns.duncanstools.craftrefill.mixin;

import me.duncanruns.duncanstools.craftrefill.CraftRefill;
import me.duncanruns.duncanstools.craftrefill.mixinint.ClearSlotsMethodOwner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {CraftingScreen.class, InventoryScreen.class})
public abstract class CraftingScreenMixin extends HandledScreen<CraftingScreenHandler> implements RecipeBookProvider {

    public CraftingScreenMixin(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!CraftRefill.moduleEnabled()) return super.keyPressed(keyCode, scanCode, modifiers);

        // Check if shift-space
        if (keyCode == GLFW.GLFW_KEY_SPACE && (modifiers & 0x1) == 1) {
            // Fill recipe if it exists
            if (CraftRefill.recipeExists()) {
                fillRecipe();
            }
            // Return
            return true;
        }
        // Otherwise do regular keypress checks
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void fillRecipe() {
        // Must use getRecipeBookWidget() method because the field name is not consistent among the 2 classes being mixed into.
        ((ClearSlotsMethodOwner) getRecipeBookWidget()).clearSlots();
        client.getNetworkHandler().sendPacket(new CraftRequestC2SPacket(client.player.currentScreenHandler.syncId, CraftRefill.getRecipe(), true));
    }

    @Inject(method = "handledScreenTick", at = @At("TAIL"))
    private void craftRefill_tickMixin(CallbackInfo info) {
        if (!CraftRefill.moduleEnabled()) return;

        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        if (CraftRefill.getRecipe() == null) {
            return;
        }
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_SPACE) && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL)) && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT))) {
            fillRecipe();
            // Null slot is fine, crafted item slot id is 0
            this.onMouseClick(null, 0, 1, SlotActionType.THROW);
        }

    }
}
