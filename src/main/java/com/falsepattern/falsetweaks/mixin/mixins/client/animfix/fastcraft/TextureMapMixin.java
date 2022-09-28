/*
 * FalseTweaks
 *
 * Copyright (C) 2022 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice, this permission notice and the word "SNEED"
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.falsetweaks.mixin.mixins.client.animfix.fastcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

//Evil black magic class #3
//Revert fastcraft ASM changes
@SuppressWarnings({"UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
@Mixin(TextureMap.class)
public abstract class TextureMapMixin {
    @Redirect(method = "loadTextureAtlas",
              at = @At(value = "INVOKE",
                       target = "Lfastcraft/HC;m(Lnet/minecraft/client/renderer/texture/Stitcher;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
                       remap = false))
    private void disableAddSpriteTweak(Stitcher stitcher, TextureAtlasSprite sprite) {
        stitcher.addSprite(sprite);
    }

    @Redirect(method = "loadTextureAtlas",
              at = @At(value = "INVOKE",
                       target = "Lfastcraft/HC;g(Lnet/minecraft/client/renderer/texture/Stitcher;Lnet/minecraft/client/renderer/texture/TextureMap;)V",
                       remap = false))
    private void disableDoStitchTweak(Stitcher stitcher, TextureMap map) {
        stitcher.doStitch();
    }

    @Redirect(method = "tick",
              at = @At(value = "INVOKE",
                       target = "Lfastcraft/HC;h(Lnet/minecraft/client/renderer/texture/TextureMap;)V",
                       remap = false))
    private void disableUpdateAnimationsTweak(TextureMap map) {
        map.updateAnimations();
    }

    @Redirect(method = "setTextureEntry",
              at = @At(value = "INVOKE",
                       target = "Lfastcraft/HC;l(Lnet/minecraft/client/renderer/texture/TextureMap;Ljava/lang/String;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
                       remap = false),
              remap = false)
    private void disableSetTextureEntryTweak(TextureMap map, String str, TextureAtlasSprite sprite) {
    }
}
