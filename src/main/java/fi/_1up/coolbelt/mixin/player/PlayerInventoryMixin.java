package fi._1up.coolbelt.mixin.player;

import com.periut.accessoryapi.api.Accessory;
import com.periut.accessoryapi.api.helper.AccessoryAccess;
import fi._1up.coolbelt.Coolbelt;
import fi._1up.coolbelt.api.ToolbeltInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.entity.player.StationFlatteningPlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements ToolbeltInventory, StationFlatteningPlayerInventory {
    @Unique
    private final static int STANDARD_DAMAGE = 1;

    @Unique
    private int coolbelt$selectedAccessorySlot = EMPTY_SLOT;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerInventory)(Object)this).player;
        if(!player.handSwinging) {
            coolbelt$setSelectedAccessory(null);
        }
    }

    @Inject(method = "scrollInHotbar", at = @At("HEAD"))
    @Environment(EnvType.CLIENT)
    public void scrollInHotbar(int _x, CallbackInfo ci) {
        coolbelt$setSelectedAccessory(null);
    }

    @Inject(method = "getSelectedItem", at = @At("HEAD"), cancellable = true)
    public void getSelectedItem(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = coolbelt$getSelectedAccessory();
        if (stack != null) cir.setReturnValue(stack);
    }

    @Inject(method = "getAttackDamage", at=@At("HEAD"), cancellable = true)
    void getAttackDamage(Entity target, CallbackInfoReturnable<Integer> cir) {
        PlayerEntity player = ((PlayerInventory)(Object)this).player;

        ItemStack selectedItem = player.inventory.getSelectedItem();
        int bestDamage = selectedItem != null ? selectedItem.getAttackDamage(target) : STANDARD_DAMAGE;

        ItemStack bestStack = null;
        for (ItemStack stack : AccessoryAccess.getAccessories(player)) {
            if (stack == null) continue;

            int damage = stack.getAttackDamage(target);
            if (damage > bestDamage) {
                bestDamage = damage;
                bestStack = stack;
            }
        }

        if (bestStack != null && bestStack.getItem() instanceof Accessory accessory) {
            ((ToolbeltInventory)player.inventory).coolbelt$setSelectedAccessory(accessory);
        }

        cir.setReturnValue(bestDamage);
    }

    @Override
    public ItemStack coolbelt$getSelectedAccessory() {
        if(coolbelt$selectedAccessorySlot < 0) return null;
        PlayerEntity player = ((PlayerInventory)(Object)this).player;
        return AccessoryAccess.getAccessory(player, coolbelt$selectedAccessorySlot);
    }

    @Override
    public void coolbelt$setSelectedAccessory(Accessory accessory) {
        if(accessory == null) {
            coolbelt$selectedAccessorySlot = EMPTY_SLOT;
            return;
        }

        PlayerEntity player = ((PlayerInventory)(Object)this).player;
        String type = accessory.getAccessoryTypes(null)[0];
        ItemStack[] stacks = AccessoryAccess.getAccessories(player, type);

        if(stacks.length > 0 && stacks[0] != null) {
            if(stacks[0].getDamage() >= stacks[0].getMaxDamage()) {
                AccessoryAccess.removeAccessory(player, stacks[0].getItem());
                coolbelt$setSelectedAccessory(null);
                return;
            }
        }

        this.coolbelt$selectedAccessorySlot = AccessoryAccess.getAccessoryInventory(player).getSlotFor(type, 0);
    }
}
