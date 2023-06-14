package me.duncanruns.duncanstools.gammaoverride.mixin;

import com.mojang.serialization.Codec;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow
    @Final
    private SimpleOption<Double> gamma;

    @Inject(method = "getGamma", at = @At("HEAD"), cancellable = true)
    private void gammaOverride_modifyGamma(CallbackInfoReturnable<SimpleOption<Double>> info) {
        info.setReturnValue(new SimpleOption<>("", null, null, null, null, null, null) {
            @Override
            public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
                return gamma.createWidget(options, x, y, width);
            }

            @Override
            public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer<Double> changeCallback) {
                return gamma.createWidget(options, x, y, width, changeCallback);
            }

            @Override
            public Double getValue() {
                double val = gamma.getValue();
                if (DuncansToolsConfig.getInstance().gammaOverrideEnabled && val == 1d) {
                    return DuncansToolsConfig.getInstance().brightGamma;
                }
                return val;
            }

            @Override
            public Codec<Double> getCodec() {
                return gamma.getCodec();
            }

            @Override
            public String toString() {
                return gamma.toString();
            }

            @Override
            public void setValue(Double value) {
                gamma.setValue(value);
            }

            @Override
            public Callbacks<Double> getCallbacks() {
                return gamma.getCallbacks();
            }
        });
    }
}
