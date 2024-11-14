package com.falsepattern.falsetweaks.modules.dynlights.base;

import com.falsepattern.falsetweaks.Compat;
import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import com.falsepattern.falsetweaks.modules.dynlights.BlockPosUtil;
import com.falsepattern.falsetweaks.modules.dynlights.DynamicLightsDrivers;
import com.falsepattern.lib.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.Getter;
import lombok.val;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DynamicLight {
    @Getter private final Entity entity;
    @Getter private final double offsetY;
    @Getter private double lastPosX = -2.14748365E9F;
    @Getter private double lastPosY = -2.14748365E9F;
    @Getter private double lastPosZ = -2.14748365E9F;
    @Getter private int lastLightLevel = 0;
    @Getter private boolean underwater = false;
    private long timeCheckMs = 0L;

    public DynamicLight(Entity entity) {
        this.entity = entity;
        this.offsetY = entity.getEyeHeight();
    }

    public void update(RenderGlobal renderGlobal) {
        val isHandLight = entity == Minecraft.getMinecraft().renderViewEntity && Compat.neodymiumActive();
        if (!isHandLight && DynamicLightsDrivers.isDynamicLightsFast()) {
            long timeNowMs = System.currentTimeMillis();
            if (timeNowMs < this.timeCheckMs + 500L) {
                return;
            }

            this.timeCheckMs = timeNowMs;
        }

        double posX = this.entity.posX - 0.5;
        double posY = this.entity.posY - 0.5 + this.offsetY;
        double posZ = this.entity.posZ - 0.5;
        int lightLevel = DynamicLights.INSTANCE.getLightLevel(this.entity);
        double dx = posX - this.lastPosX;
        double dy = posY - this.lastPosY;
        double dz = posZ - this.lastPosZ;
        double delta = 0.1;
        if (!(Math.abs(dx) <= delta) || !(Math.abs(dy) <= delta) || !(Math.abs(dz) <= delta) || this.lastLightLevel != lightLevel) {
            this.underwater = false;
            World world = renderGlobal.theWorld;
            if (world != null) {
                Block block = world.getBlock(MathUtil.floor(posX), MathUtil.floor(posY), MathUtil.floor(posZ));
                this.underwater = block == Blocks.water;
            }

            if (!isHandLight) {
                if (lightLevel > 0) {
                    int distance = lightLevel + 1;
                    renderGlobal.markBlockRangeForRenderUpdate((int) (posX - distance), (int) (posY - distance), (int) (posZ - distance),
                                                               (int) (posX + distance), (int) (posY + distance), (int) (posZ + distance));
                }

                this.updateLitChunks(renderGlobal);
            }
            this.lastPosX = posX;
            this.lastPosY = posY;
            this.lastPosZ = posZ;
            this.lastLightLevel = lightLevel;
        }
    }

    public void updateLitChunks(RenderGlobal renderGlobal) {
        int distance = lastLightLevel + 1;
        renderGlobal.markBlockRangeForRenderUpdate((int) (lastPosX - distance), (int) (lastPosY - distance), (int) (lastPosZ - distance),
                                                   (int) (lastPosX + distance), (int) (lastPosY + distance), (int) (lastPosZ + distance));
    }

    public String toString() {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}