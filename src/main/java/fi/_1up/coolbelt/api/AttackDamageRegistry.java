package fi._1up.coolbelt.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

/// Registry managing attack damage evaluations for [ItemStack] instances against target [Entity] instances.
/// Coolbelt will generally prefer the highest attack damage out of the evaluated providers.
@ApiStatus.Experimental
public final class AttackDamageRegistry {
    /// Default attack damage value for bare hand attacks.
    public static final int STANDARD_ATTACK_DAMAGE = 1;
    /// Sentinel value indicating an entity cannot be damaged by the item stack.
    public static final int UNDAMAGEABLE = Integer.MIN_VALUE;

    /// Internal [ItemEvalRegistry] instance mapping [ItemStack] and [Entity] references to attack damage values.
    private static final ItemEvalRegistry<Entity, Integer> REGISTRY = new ItemEvalRegistry<>(UNDAMAGEABLE) {};

    static {
        // Hand provider
        register(ItemEvalRegistry.PRIORITY_LOW, (_stack, _target) -> Optional.of(STANDARD_ATTACK_DAMAGE));

        // Vanilla provider
        register(ItemEvalRegistry.PRIORITY_VANILLA, (stack, target) -> {
            if (stack == null) return Optional.empty();
            return Optional.of(stack.getAttackDamage(target));
        });
    }

    /// Private constructor to prevent instantiation.
    private AttackDamageRegistry() {}

    /// Registers an [ItemEvalProvider] using default [ItemEvalRegistry#PRIORITY_VANILLA].
    /// @param provider [ItemEvalProvider] implementation to add.
    @SuppressWarnings("unused")
    public static void register(@NotNull ItemEvalProvider<Entity, Integer> provider) {
        REGISTRY.register(provider);
    }

    /// Registers an [ItemEvalProvider] with a specified priority.
    /// @param priority Numerical priority determining execution order.
    /// @param provider [ItemEvalProvider] implementation to add.
    public static void register(int priority, @NotNull ItemEvalProvider<Entity, Integer> provider) {
        REGISTRY.register(priority, provider);
        LOGGER.info("Attack damage provider registered with priority %d.", priority);
    }

    /// Evaluates the attack damage output for an [ItemStack] against a target [Entity].
    /// Coolbelt will generally prefer the highest attack damage out of the evaluated providers.
    /// @param stack [ItemStack] being evaluated.
    /// @param target Target [Entity].
    /// @return Attack damage value returned by the highest priority provider, or [UNDAMAGEABLE] if none match.
    public static int getDamage(@Nullable ItemStack stack, @NotNull Entity target) {
        return REGISTRY.evaluate(stack, target);
    }
}
