package fi._1up.coolbelt.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/// Evaluator interface that calculates a result for a given [ItemStack] and target object.
/// @param <T> Target object type.
/// @param <R> Return type.
@ApiStatus.Experimental
@FunctionalInterface
public interface ItemEvalProvider<T, R> {
    /// Evaluates the given [ItemStack] and target object.
    /// @param stack Item stack being evaluated.
    /// @param target Target object.
    /// @return [Optional] containing the result, or an empty [Optional] if evaluation yields no value.
    Optional<R> get(@Nullable ItemStack stack, @NotNull T target);
}
