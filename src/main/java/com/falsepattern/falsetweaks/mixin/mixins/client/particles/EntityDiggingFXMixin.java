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

package com.falsepattern.falsetweaks.mixin.mixins.client.particles;

import com.falsepattern.falsetweaks.modules.particles.ParticleUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

@Mixin(EntityDiggingFX.class)
public abstract class EntityDiggingFXMixin extends EntityFX {
    @Shadow
    private Block field_145784_a;

    protected EntityDiggingFXMixin(World p_i1218_1_, double p_i1218_2_, double p_i1218_4_, double p_i1218_6_) {
        super(p_i1218_1_, p_i1218_2_, p_i1218_4_, p_i1218_6_);
    }

    @Inject(method = "renderParticle",
            at = @At("HEAD"),
            require = 1,
            cancellable = true)
    private void cubeParticle(Tessellator tess, float partialTick, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY, CallbackInfo ci) {
        if (!field_145784_a.isNormalCube()) {
            return;
        }
        ci.cancel();
        float u1 = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float u2 = u1 + 0.015609375F;
        float v1 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float v2 = v1 + 0.015609375F;
        float scale = 0.1F * this.particleScale;
        if (this.particleIcon != null) {
            u1 = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
            u2 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
            v1 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
            v2 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTick - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTick - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTick - interpPosZ);

        ParticleUtil.drawCube(tess, this.particleRed, this.particleGreen, this.particleBlue, u1, u2, v1, v2, scale, x, y, z);
    }
}
