package com.falsepattern.falsetweaks.mixin.mixins.client.dynlights;

import com.falsepattern.falsetweaks.api.dynlights.FTDynamicLights;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IWorldAccess;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin implements IWorldAccess {
    @Inject(method = "setWorldAndLoadRenderers",
            at = @At(value = "HEAD"),
            require = 1)
    private void clearDynLights(WorldClient worldClient, CallbackInfo ci) {
        val dl = FTDynamicLights.frontend();
        if (dl.enabled())
            dl.clear();
    }

    @Inject(method = "loadRenderers",
            at = @At(value = "HEAD"),
            require = 1)
    private void clearDynLights2(CallbackInfo ci) {
        val dl = FTDynamicLights.frontend();
        if (dl.enabled())
            dl.clear();
    }

    @Inject(method = "sortAndRender",
            at = @At("HEAD"),
            require = 1)
    private void updateDynLights(EntityLivingBase entityLiving, int renderPass, double partialTickTime, CallbackInfoReturnable<Integer> cir) {
        val dl = FTDynamicLights.frontend();
        if (dl.enabled())
            dl.update((RenderGlobal) (Object) this);
    }

    @Override
    public void onEntityCreate(Entity entity) {
        val dl = FTDynamicLights.frontend();
        if (dl.enabled())
            dl.entityAdded(entity, (RenderGlobal) (Object) this);
    }

    @Override
    public void onEntityDestroy(Entity entity) {
        val dl = FTDynamicLights.frontend();
        if (dl.enabled())
            dl.entityRemoved(entity, (RenderGlobal) (Object) this);
    }
}