package fi._1up.coolbelt.mixin.gui;

import fi._1up.coolbelt.api.DurabilityHudGroup;
import fi._1up.coolbelt.api.DurabilityHudRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static fi._1up.coolbelt.config.CoolbeltConfig.config;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private static ItemRenderer ITEM_RENDERER;
    @Shadow private Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/Lighting;turnOff()V"))
    private void render(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (!config.hud.showDurabilityHUD) return;

        ScreenScaler scaler = new ScreenScaler(minecraft.options, minecraft.displayWidth, minecraft.displayHeight);
        int x = scaler.getScaledWidth() / 2 - 8;
        int y = scaler.getScaledHeight() - 19;

        for (DurabilityHudGroup group : DurabilityHudRegistry.getGroups()) {
            List<ItemStack> stacks = group.stackSupplier().apply(minecraft.player);
            renderDurabilities(stacks, x + group.x(), y + group.y(), group.stepX(), group.stepY());
        }
    }

    @Unique
    private void renderDurabilities(List<ItemStack> stacks, int x, int y, int stepX, int stepY) {
        for (ItemStack stack : stacks) {
            if (stack != null) {
                ITEM_RENDERER.renderGuiItem(minecraft.textRenderer, minecraft.textureManager, stack, x, y);
                ITEM_RENDERER.renderGuiItemDecoration(minecraft.textRenderer, minecraft.textureManager, stack, x, y);
            }
            x += stepX;
            y += stepY;
        }
    }
}
