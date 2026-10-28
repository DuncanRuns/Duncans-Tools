package me.duncanruns.duncanstools.gammaoverride.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapRenderStateExtractorMixin {
    @WrapOperation(method = "extract", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 0))
    private float replaceGamma(Double instance, Operation<Float> original) {
        if (DuncansToolsConfig.getInstance().gammaOverrideEnabled) {
            return (float) DuncansToolsConfig.getInstance().brightGamma;
        }
        return original.call(instance);
    }
}
