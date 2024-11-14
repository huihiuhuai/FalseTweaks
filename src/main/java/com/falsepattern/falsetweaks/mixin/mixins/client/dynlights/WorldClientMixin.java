package com.falsepattern.falsetweaks.mixin.mixins.client.dynlights;

import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import com.falsepattern.falsetweaks.modules.dynlights.DynamicLightsWorldClient;
import lombok.val;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World implements DynamicLightsWorldClient {
    @Shadow @Final private Minecraft mc;
    @Dynamic
    private boolean ft$renderItemInFirstPerson;
    public WorldClientMixin(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int min) {
        int light = super.getLightBrightnessForSkyBlocks(x, y, z, min);
        val dl = FTDynamicLights.frontend();
        if (dl.enabled()) {
            if (ft$renderItemInFirstPerson()) {
                light = dl.getCombinedLight(mc.renderViewEntity, light);
            }

            if (!getBlock(x, y, z).isOpaqueCube()) {
                light = dl.getCombinedLight(x, y, z, light);
            }
        }
        return light;
    }

    @Override
    public void ft$renderItemInFirstPerson(boolean value) {
        ft$renderItemInFirstPerson = value;
    }
}