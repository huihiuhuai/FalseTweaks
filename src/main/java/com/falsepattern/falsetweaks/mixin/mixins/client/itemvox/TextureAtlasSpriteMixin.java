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

package com.falsepattern.falsetweaks.mixin.mixins.client.itemvox;

import com.falsepattern.falsetweaks.config.VoxelizerConfig;
import com.falsepattern.falsetweaks.modules.voxelizer.Layer;
import com.falsepattern.falsetweaks.modules.voxelizer.VoxelMesh;
import com.falsepattern.falsetweaks.modules.voxelizer.interfaces.ITextureAtlasSpriteMixin;
import lombok.Getter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements ITextureAtlasSpriteMixin {
    @Getter
    @Shadow
    protected int frameCounter;

    @Shadow public abstract int[][] getFrameTextureData(int p_147965_1_);

    @Shadow public abstract int getFrameCount();

    private VoxelMesh voxelMesh;

    @Inject(method = "clearFramesTextureData",
            at = @At(value = "HEAD"),
            require = 1)
    private void compileStatic(CallbackInfo ci) {
        voxelMesh = new VoxelMesh(VoxelizerConfig.MESH_OPTIMIZATION_STRATEGY_PRESET.strategy, new Layer((TextureAtlasSprite)(Object)this, 0.0625F));
        voxelMesh.compile();
    }

    @Override
    public VoxelMesh getVoxelMesh() {
        return voxelMesh;
    }

    @Override
    public void setVoxelMesh(VoxelMesh mesh) {
        voxelMesh = mesh;
    }

    @Override
    public int[][] getFrameTextureDataSafe(int id) {
        id %= getFrameCount();
        return getFrameTextureData(id);
    }
}
