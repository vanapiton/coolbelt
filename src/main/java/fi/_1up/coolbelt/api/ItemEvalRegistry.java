package fi._1up.coolbelt.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ItemEvalRegistry<T, R> {
    public static final int PRIORITY_LOW = 100;
    public static final int PRIORITY_VANILLA = 400;
    public static final int PRIORITY_MEDIUM = 500;
    public static final int PRIORITY_HIGH = 1000;

    private final List<PrioritizedEntry<T, R>> providers = new CopyOnWriteArrayList<>();
    private final R defaultValue;

    protected record PrioritizedEntry<T, R>(
            int priority,
            ItemEvalProvider<T, R> provider
    ) {}

    protected ItemEvalRegistry(R defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected void register(@NotNull ItemEvalProvider<T, R> provider) {
        register(PRIORITY_MEDIUM, provider);
    }

    protected void register(int priority, @NotNull ItemEvalProvider<T, R> provider) {
        providers.add(new PrioritizedEntry<>(priority, provider));
        providers.sort(Comparator.comparing(PrioritizedEntry<T, R>::priority).reversed());
    }

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