package me.duncanruns.duncanstools.farmclicker.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccess {
    @Invoker("startAttack")
    @SuppressWarnings("UnusedReturnValue")
    boolean invokeStartAttack();

    @Accessor("missTime")
    void setMissTime(int cooldown);
}
