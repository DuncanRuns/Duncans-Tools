package me.duncanruns.duncanstools.alignmentlocker.mixin;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import net.minecraft.world.entity.Entity;
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
    public abstract float getYRot();

    @Shadow
    public abstract void setYRot(float yaw);

    @Inject(method = "turn", at = @At("HEAD"))
    private void alignmentLocker_changeLookDirectionStartMixin(double xo, double yo, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        lockedYaw = getYRot();
    }

    @Inject(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setYRot(F)V", shift = At.Shift.AFTER))
    private void alignmentLocker_changeLookDirectionYawMixin(double xo, double yo, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        if (AlignmentLocker.alignLock) {
            setYRot(lockedYaw);
        }
    }
}
