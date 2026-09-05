package fi._1up.coolbelt.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@FunctionalInterface
public interface ItemEvalProvider<T, R> {
    Optional<R> get(@Nullable ItemStack stack, @NotNull T target);
}
