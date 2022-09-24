/*
 * Triangulator
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

package com.falsepattern.falsetweaks.mixin.mixins.client.vanilla.itemvox;

import com.falsepattern.falsetweaks.TriCompat;
import com.falsepattern.falsetweaks.VoxelRenderListManager;
import com.falsepattern.falsetweaks.config.FTConfig;
import com.falsepattern.falsetweaks.voxelizer.VoxelMesh;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemIn2D(Lnet/minecraft/client/renderer/Tessellator;FFFFIIF)V",
                     ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1)
    private void voxelizedRenderer(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, IItemRenderer.ItemRenderType type,
                                   CallbackInfo ci,
                                   TextureManager texturemanager, Item item, Block block, IItemRenderer customRenderer, IIcon iicon, Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5, float f6) {
        val mesh = VoxelMesh.getMesh((TextureAtlasSprite) iicon);
        if (FTConfig.ENABLE_ITEM_RENDERLISTS && VoxelRenderListManager.INSTANCE.pre(mesh)) {
            return;
        }
        val tess = TriCompat.tessellator();
        tess.startDrawingQuads();
        mesh.renderToTessellator(tess);
        tess.draw();
        if (FTConfig.ENABLE_ITEM_RENDERLISTS) {
            VoxelRenderListManager.INSTANCE.post();
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemIn2D(Lnet/minecraft/client/renderer/Tessellator;FFFFIIF)V",
                       ordinal = 0),
              require = 1)
    private void voxelizedRendererKillOriginal(Tessellator tess, float a, float b, float c, float d, int e, int f, float g) {

    }
}