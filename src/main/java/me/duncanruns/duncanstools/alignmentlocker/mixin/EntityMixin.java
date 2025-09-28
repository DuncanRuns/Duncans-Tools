package me.duncanruns.duncanstools.alignmentlocker.mixin;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private float lockedYaw;

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract void setYaw(float yaw);

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void alignmentLocker_changeLookDirectionStartMixin(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        lockedYaw = getYaw();
    }

    @Inject(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setYaw(F)V", shift = At.Shift.AFTER))
    private void alignmentLocker_changeLookDirectionYawMixin(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        if (AlignmentLocker.alignLock) {
            setYaw(lockedYaw);
        }
    }
}
