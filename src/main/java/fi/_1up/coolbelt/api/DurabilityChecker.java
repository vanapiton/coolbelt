package fi._1up.coolbelt.api;

import net.minecraft.item.ItemStack;

/// Allows for checking the durability of an [ItemStack].
public final class DurabilityChecker {
    /// Checks if the an [ItemStack] is at or below a given durability.
    /// @param stack The [ItemStack] to check.
    /// @param durability The durability value to check against.
    /// @return Boolean indicating if the [ItemStack] is at or below the durability.
    public static boolean isAtOrBelow(ItemStack stack, int durability) {
        return stack != null && stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - durability;
    }

    /// Private constructor to prevent instantiation.
    private DurabilityChecker() {}
}
