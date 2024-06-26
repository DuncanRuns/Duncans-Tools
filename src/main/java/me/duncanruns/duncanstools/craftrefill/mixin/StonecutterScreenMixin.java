package me.duncanruns.duncanstools.craftrefill.mixin;

import me.duncanruns.duncanstools.craftrefill.CraftRefill;
import me.duncanruns.duncanstools.craftrefill.StonecuttingRecipeInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends HandledScreen<StonecutterScreenHandler> {
    public StonecutterScreenMixin(StonecutterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @ModifyArg(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickButton(II)V"), index = 1)
    private int getButtonId(int buttonId) {
        List<RecipeEntry<StonecuttingRecipe>> availableRecipes = handler.getAvailableRecipes();
        if (buttonId < availableRecipes.size())
            CraftRefill.lastSCRecipe = new StonecuttingRecipeInfo(getScreenHandler().input.getStack(0).getItem(), buttonId, availableRecipes.get(buttonId).value().getResult(null).getItem());
        return buttonId;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!CraftRefill.moduleEnabled()) return super.keyPressed(keyCode, scanCode, modifiers);

        // Check if shift-space
        if (keyCode == GLFW.GLFW_KEY_SPACE && (modifiers & 0x1) == 1) {
            // Fill recipe if it exists
            if (CraftRefill.lastSCRecipe != null) {
                fillRecipe();
            }
            // Return
            return true;
        }
        // Otherwise do regular keypress checks
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void handledScreenTick() {
        if (!CraftRefill.moduleEnabled()) return;

        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        if (CraftRefill.lastSCRecipe != null) {
            if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_SPACE) && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL)) && (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT))) {
                fillRecipe();
                // Null slot is fine, crafted item slot id is 1
                this.onMouseClick(null, 1, 0, SlotActionType.QUICK_MOVE);
                // Throw results out
                for (Slot slot : getScreenHandler().slots) {
                    if (!slot.getStack().getComponentChanges().isEmpty()) {
                        continue;
                    }
                    if (slot.getStack().getItem().equals(CraftRefill.lastSCRecipe.outputItem())) {
                        this.onMouseClick(slot, 0, 1, SlotActionType.THROW);
                    }
                }
            }
        }
    }

    @Unique
    private void fillRecipe() {
        if (fillSlot()) {
            selectRecipe();
        }
    }

    @Unique
    private boolean fillSlot() {
        // If there is already the item in there, skip
        if (CraftRefill.lastSCRecipe.inputItem().equals(getScreenHandler().input.getStack(0).getItem())) {
            return true;
        }

        // Throw out incorrect item in input
        if (getScreenHandler().input.getStack(0).getCount() > 0) {
            this.onMouseClick(null, 0, 1, SlotActionType.THROW);
        }

        // Find item
        Slot bestSlot = null;
        for (Slot slot : getScreenHandler().slots) {
            if (!slot.getStack().getComponentChanges().isEmpty()) {
                continue;
            }
            if (slot.getStack().getItem().equals(CraftRefill.lastSCRecipe.inputItem())) {
                if (bestSlot == null || (slot.getStack().getCount() > bestSlot.getStack().getCount())) {
                    bestSlot = slot;
                }
            }
        }

        // If none found, return
        if (bestSlot == null) {
            return false;
        }
        onMouseClick(bestSlot, 0, 0, SlotActionType.QUICK_MOVE);
        return true;
    }

    @Unique
    private void selectRecipe() {
        client.interactionManager.clickButton(this.handler.syncId, CraftRefill.lastSCRecipe.buttonId());
    }
}
