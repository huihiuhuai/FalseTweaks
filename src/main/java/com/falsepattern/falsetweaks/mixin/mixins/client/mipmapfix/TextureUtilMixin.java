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

package com.falsepattern.falsetweaks.mixin.mixins.client.mipmapfix;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.TextureUtil;

@Mixin(TextureUtil.class)
public abstract class TextureUtilMixin {
    private static ThreadLocal<int[]> threadLocalBuffer;

    @Inject(method = "<clinit>",
            at = @At("RETURN"),
            require = 1)
    private static void setupThreadLocal(CallbackInfo ci) {
        threadLocalBuffer = ThreadLocal.withInitial(() -> new int[4]);
    }

    @Redirect(method = "func_147943_a",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/texture/TextureUtil;field_147957_g:[I",
                       opcode = Opcodes.GETSTATIC),
              require = 9)
    private static int[] swapReferencesWithThreadLocal() {
        return threadLocalBuffer.get();
    }
}
