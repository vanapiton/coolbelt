package fi._1up.coolbelt.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/// Registry for evaluating [ItemStack] instances against target objects using prioritized [ItemEvalProvider] instances.
/// @param <T> Target object type.
/// @param <R> Return type.
@ApiStatus.Experimental
public abstract class ItemEvalRegistry<T, R> {
    /// Lowest priority level, lower than [PRIORITY_VANILLA].
    public static final int PRIORITY_LOW = 100;
    /// Baseline priority level for vanilla behavior.
    public static final int PRIORITY_VANILLA = 400;
    /// Default priority level for registered providers, higher than [PRIORITY_VANILLA].
    public static final int PRIORITY_DEFAULT = 500;
    /// High priority level, higher than [PRIORITY_DEFAULT].
    public static final int PRIORITY_HIGH = 1000;
    /// Highest priority level, higher than [PRIORITY_HIGH].
    public static final int PRIORITY_CRITICAL = 1500;

    /// [List] of registered [ItemEvalProvider] entries ordered by priority.
    private final List<PrioritizedEntry<T, R>> providers = new CopyOnWriteArrayList<>();
    /// Default result returned when no provider produces a value.
    private final R defaultValue;

    /// Container holding an [ItemEvalProvider] and its numerical priority.
    /// @param priority Numerical priority determining execution order.
    /// @param provider [ItemEvalProvider] instance handling evaluation.
    /// @param <T> Target object type.
    /// @param <R> Return type.
    protected record PrioritizedEntry<T, R>(
            int priority,
            ItemEvalProvider<T, R> provider
    ) {}

    /// Constructs the registry with a fallback return value.
    /// @param defaultValue Fallback value returned when all evaluations yield empty results.
    protected ItemEvalRegistry(R defaultValue) {
        this.defaultValue = defaultValue;
    }

    /// Registers an [ItemEvalProvider] using default [PRIORITY_DEFAULT].
    /// @param provider [ItemEvalProvider] implementation to add.
    protected void register(@NotNull ItemEvalProvider<T, R> provider) {
        register(PRIORITY_DEFAULT, provider);
    }

    /// Registers an [ItemEvalProvider] with a designated priority.
    /// @param priority Numerical priority determining execution order.
    /// @param provider [ItemEvalProvider] implementation to add.
    protected void register(int priority, @NotNull ItemEvalProvider<T, R> provider) {
        providers.add(new PrioritizedEntry<>(priority, provider));
        providers.sort(Comparator.comparing(PrioritizedEntry<T, R>::priority).reversed());
    }

    /// Evaluates registered [ItemEvalProvider] instances sequentially in descending priority order until a non-empty result is produced.
    /// @param stack [ItemStack] being evaluated.
    /// @param target Target object.
    /// @return Return type value from the highest priority matching provider, or [defaultValue] if none match.
    protected R evaluate(@Nullable ItemStack stack, @NotNull T target) {
        for (PrioritizedEntry<T, R> entry : providers) {
            Optional<R> result = entry.provider().get(stack, target);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return defaultValue;
    }
}
