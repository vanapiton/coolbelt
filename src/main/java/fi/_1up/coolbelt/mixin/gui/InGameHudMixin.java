package fi._1up.coolbelt.mixin.gui;

import fi._1up.coolbelt.api.ToolbeltInventory;
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

import static fi._1up.coolbelt.config.CoolbeltConfig.config;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow private static ItemRenderer ITEM_RENDERER;
    @Shadow private Minecraft minecraft;


    @Inject(method = "render", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/Lighting;turnOff()V"))
    private void render(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        ItemStack stack = ((ToolbeltInventory)minecraft.player.inventory).coolbelt$getSelectedAccessory();
        if(stack != null) renderDurabilityToast(stack);
    }

    @Unique
    private void renderDurabilityToast(ItemStack stack) {
        if(!config.showDurabilityToast) return;

        ScreenScaler scaler = new ScreenScaler(minecraft.options, minecraft.displayWidth, minecraft.displayHeight);
        int x = scaler.getScaledWidth() / 2 + 112;
        int y = scaler.getScaledHeight() - 19;

        ITEM_RENDERER.renderGuiItem(minecraft.textRenderer, minecraft.textureManager, stack, x, y);
        ITEM_RENDERER.renderGuiItemDecoration(minecraft.textRenderer, minecraft.textureManager, stack, x, y);
    }
}
