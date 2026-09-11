package fi._1up.coolbelt.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

public final class AttackDamageRegistry {
    public static final int STANDARD_ATTACK_DAMAGE = 1;
    public static final int UNDAMAGEABLE = Integer.MIN_VALUE;

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

    private AttackDamageRegistry() {}

    @SuppressWarnings("unused")
    public static void register(@NotNull ItemEvalProvider<Entity, Integer> provider) {
        REGISTRY.register(provider);
    }

    public static void register(int priority, @NotNull ItemEvalProvider<Entity, Integer> provider) {
        REGISTRY.register(priority, provider);
        LOGGER.info("Attack damage provider registered with priority %d.", priority);
    }

    public static int getDamage(@Nullable ItemStack stack, @NotNull Entity target) {
        return REGISTRY.evaluate(stack, target);
    }
}
