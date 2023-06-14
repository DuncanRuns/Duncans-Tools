package me.duncanruns.duncanstools.farmclicker.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccess {
    @Invoker
    void invokeDoItemUse();

    @Invoker
    boolean invokeDoAttack();

    @Accessor
    void setAttackCooldown(int cooldown);
}
