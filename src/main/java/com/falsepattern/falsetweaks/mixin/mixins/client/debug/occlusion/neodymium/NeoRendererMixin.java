/*
 * This file is part of FalseTweaks.
 *
 * Copyright (C) 2022-2024 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * FalseTweaks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FalseTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FalseTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package com.falsepattern.falsetweaks.mixin.mixins.client.debug.occlusion.neodymium;

import com.falsepattern.falsetweaks.modules.debug.Debug;
import com.falsepattern.falsetweaks.modules.occlusion.OcclusionCompat;
import makamys.neodymium.renderer.NeoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.WorldRenderer;

@Mixin(value = NeoRenderer.class,
       remap = false)
public abstract class NeoRendererMixin {
    @Inject(method = "preRenderSortedRenderers",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void noShadow(int renderPass, double alpha, WorldRenderer[] sortedWorldRenderers, CallbackInfoReturnable<Integer> cir) {
        if (Debug.ENABLED && !Debug.shadowPass && OcclusionCompat.OptiFineCompat.isShadowPass()) {
            cir.setReturnValue(0);
        }
    }
}
