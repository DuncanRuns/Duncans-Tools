package me.duncanruns.duncanstools.gammaoverride.mixin;

import com.mojang.serialization.Codec;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow
    @Final
    private OptionInstance<Double> gamma;

    @Inject(method = "gamma", at = @At("HEAD"), cancellable = true)
    private void gammaOverride_modifyGamma(CallbackInfoReturnable<OptionInstance<Double>> info) {
        info.setReturnValue(new OptionInstance<Double>("", null, null, null, null, null, null) {
            @Override
            public @NonNull AbstractWidget createButton(@NonNull Options options, int x, int y, int width) {
                return gamma.createButton(options, x, y, width);
            }

            @Override
            public @NonNull AbstractWidget createButton(@NonNull Options options, int x, int y, int width, @NonNull Consumer<Double> changeCallback) {
                return gamma.createButton(options, x, y, width, changeCallback);
            }

            @Override
            public @NonNull Double get() {
                double val = gamma.get();
                if (DuncansToolsConfig.getInstance().gammaOverrideEnabled) {
                    return DuncansToolsConfig.getInstance().brightGamma;
                }
                return val;
            }

            @Override
            public @NonNull Codec<Double> codec() {
                return gamma.codec();
            }

            @Override
            public @NonNull String toString() {
                return gamma.toString();
            }

            @Override
            public void set(@NonNull Double value) {
                gamma.set(value);
            }

            @Override
            public OptionInstance.@NonNull ValueSet<Double> values() {
                return gamma.values();
            }
        });
    }
}
