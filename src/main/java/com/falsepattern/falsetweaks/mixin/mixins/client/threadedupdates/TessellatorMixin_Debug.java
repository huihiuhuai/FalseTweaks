package com.falsepattern.falsetweaks.mixin.mixins.client.threadedupdates;

import com.falsepattern.falsetweaks.modules.threadedupdates.ThreadedChunkUpdateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.Tessellator;

@Mixin(value = Tessellator.class)
public abstract class TessellatorMixin_Debug {

    @Inject(method = {"setVertexState", "func_154352_a", "setColorRGBA_F", "setColorRGBA", "startDrawing",
                      "addTranslation", "setTranslation", "addVertexWithUV", "setNormal", "setColorOpaque", "addVertex",
                      "setColorOpaque_I", "reset", "setBrightness", "startDrawingQuads", "disableColor",
                      "setColorRGBA_I", "setTextureUV", "setColorOpaque_F"},
            at = @At("HEAD"),
            require = 1)
    private void verifyThreadIsCorrect(CallbackInfo ci) {
        verifyThreadIsCorrect();
    }

    @Inject(method = {"getVertexState", "draw"},
            at = @At("HEAD"),
            require = 1)
    private void verifyThreadIsCorrect(CallbackInfoReturnable cir) {
        verifyThreadIsCorrect();
    }

    @Unique
    private void verifyThreadIsCorrect() {
        if (((Object) this) == Tessellator.instance) {
            if (ThreadedChunkUpdateHelper.MAIN_THREAD != null &&
                Thread.currentThread() != ThreadedChunkUpdateHelper.MAIN_THREAD) {
                throw new IllegalStateException("Tried to access main tessellator from non-main thread " + Thread.currentThread().getName());
            }
        }
    }

}
