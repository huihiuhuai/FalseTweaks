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

package com.falsepattern.falsetweaks.modules.voxelizer;

import com.falsepattern.falsetweaks.config.VoxelizerConfig;
import com.falsepattern.falsetweaks.mixin.helper.ITextureAtlasSpriteMixin;
import com.falsepattern.falsetweaks.modules.voxelizer.strategy.MergingStrategy;
import lombok.val;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.List;
import java.util.Objects;

public class VoxelMesh {
    public static final float EPSILON = 0.0001f;
    private final MergingStrategy strategy;
    private final Layer[] layers;
    private final float[] xOffsets;
    private final float[] yOffsets;
    private final float[] zOffsets;
    public final float totalThickness;
    private final VoxelCompiler compiler;
    private List<Face> faceCache;
    private String cacheIdentity = null;

    public VoxelMesh(MergingStrategy strategy, Layer... layers) {
        this.strategy = strategy;
        this.layers = layers;
        this.compiler = new VoxelCompiler(layers);
        xOffsets = new float[compiler.xSize + 1];
        yOffsets = new float[compiler.ySize + 1];
        zOffsets = new float[compiler.zSize + 1];
        float offset = 0;
        for (int z = compiler.zSize - 1; z >= 0; z--) {
            zOffsets[z + 1] = offset;
            offset -= layers[z].thickness;
        }
        zOffsets[0] = offset;
        totalThickness = offset;
        for (int x = 0; x <= compiler.xSize; x++) {
            xOffsets[x] = 1 - x / (float)compiler.xSize;
        }
        for (int y = 0; y <= compiler.ySize; y++) {
            yOffsets[y] = 1 - y / (float)compiler.ySize;
        }
    }

    public static VoxelMesh getMesh(TextureAtlasSprite iicon) {
        val texture = (ITextureAtlasSpriteMixin) iicon;
        VoxelMesh mesh = texture.getVoxelMesh();
        if (mesh == null) {
            mesh = new VoxelMesh(VoxelizerConfig.MESH_OPTIMIZATION_STRATEGY_PRESET.strategy, new Layer(iicon, 0.0625F));
            texture.setVoxelMesh(mesh);
        }
        return mesh;
    }

    public void renderToTessellator(Tessellator tess, int overlayLayer, boolean remapUV) {
        compile();
        for (val face: faceCache) {
            float u1, v1, u2, v2;
            float EPSILON_OUT = overlayLayer * EPSILON;
            if (remapUV) {
                u1 = xOffsets[face.minX];
                v1 = yOffsets[face.minY];
                u2 = xOffsets[face.maxX + 1];
                v2 = yOffsets[face.maxY + 1];
            } else {
                u1 = face.u1;
                v1 = face.v1;
                u2 = face.u2;
                v2 = face.v2;
            }
            switch (face.dir) {
                case Front: {
                    tess.setNormal(0, 0, 1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON, yOffsets[face.maxY + 1] - EPSILON, zOffsets[face.z + 1] + EPSILON_OUT, u1, v2);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON, yOffsets[face.minY] + EPSILON, zOffsets[face.z + 1] + EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON, yOffsets[face.minY] + EPSILON, zOffsets[face.z + 1] + EPSILON_OUT, u2, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON, yOffsets[face.maxY + 1] - EPSILON, zOffsets[face.z + 1] + EPSILON_OUT, u2, v2);
                    break;
                }
                case Back: {
                    tess.setNormal(0, 0, -1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON, yOffsets[face.minY] + EPSILON, zOffsets[face.z] - EPSILON_OUT, u2, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON, yOffsets[face.minY] + EPSILON, zOffsets[face.z] - EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON, yOffsets[face.maxY + 1] - EPSILON, zOffsets[face.z] - EPSILON_OUT, u1, v2);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON, yOffsets[face.maxY + 1] - EPSILON, zOffsets[face.z] - EPSILON_OUT, u2, v2);
                    break;
                }
                case Left: {
                    tess.setNormal(1, 0, 0);
                    tess.addVertexWithUV(xOffsets[face.minX] - EPSILON + EPSILON_OUT, yOffsets[face.maxY + 1] - EPSILON - EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v2);
                    tess.addVertexWithUV(xOffsets[face.minX] - EPSILON + EPSILON_OUT, yOffsets[face.maxY + 1] - EPSILON - EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v2);
                    tess.addVertexWithUV(xOffsets[face.minX] - EPSILON + EPSILON_OUT, yOffsets[face.minY] + EPSILON + EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] - EPSILON + EPSILON_OUT, yOffsets[face.minY] + EPSILON + EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v1);
                    break;
                }
                case Right: {
                    tess.setNormal(-1, 0, 0);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] + EPSILON - EPSILON_OUT, yOffsets[face.minY] + EPSILON + EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] + EPSILON - EPSILON_OUT, yOffsets[face.minY] + EPSILON + EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] + EPSILON - EPSILON_OUT, yOffsets[face.maxY + 1] - EPSILON - EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v2);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] + EPSILON - EPSILON_OUT, yOffsets[face.maxY + 1] - EPSILON - EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v2);
                    break;
                }
                case Up: {
                    tess.setNormal(0, 1, 0);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON + EPSILON_OUT, yOffsets[face.minY] - EPSILON + EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON + EPSILON_OUT, yOffsets[face.minY] - EPSILON + EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON - EPSILON_OUT, yOffsets[face.minY] - EPSILON + EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u2, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON - EPSILON_OUT, yOffsets[face.minY] - EPSILON + EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u2, v1);
                    break;
                }
                case Down: {
                    tess.setNormal(0, -1, 0);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON - EPSILON_OUT, yOffsets[face.maxY + 1] + EPSILON - EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u2, v1);
                    tess.addVertexWithUV(xOffsets[face.maxX + 1] - EPSILON - EPSILON_OUT, yOffsets[face.maxY + 1] + EPSILON - EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u2, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON + EPSILON_OUT, yOffsets[face.maxY + 1] + EPSILON - EPSILON_OUT, zOffsets[face.z] - EPSILON - EPSILON_OUT, u1, v1);
                    tess.addVertexWithUV(xOffsets[face.minX] + EPSILON + EPSILON_OUT, yOffsets[face.maxY + 1] + EPSILON - EPSILON_OUT, zOffsets[face.z + 1] + EPSILON + EPSILON_OUT, u1, v1);
                    break;
                }
            }
        }
    }

    public void compile() {
        String currentIdentity = getIdentity(0, false);
        if (!Objects.equals(cacheIdentity, currentIdentity)) {
            faceCache = compiler.compile(strategy);
            cacheIdentity = currentIdentity;
        }
    }

    public String getIdentity(int overlayLayer, boolean remapUV) {
        val result = new StringBuilder();
        if (remapUV) {
            result.append("remap_uv!");
        }
        if (overlayLayer > 0) {
            result.append("overlay").append(overlayLayer).append("!");
        }
        for (val layer: layers) {
            result.append(layer.textureIdentity()).append('&');
        }
        return result.toString();
    }

}
