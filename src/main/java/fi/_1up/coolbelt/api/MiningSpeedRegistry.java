package fi._1up.coolbelt.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;
import static fi._1up.coolbelt.config.CoolbeltConfig.CONFIG;

public final class MiningSpeedRegistry {
    public static final float STANDARD_MINING_SPEED = 1.0f;
    public static final float STANDARD_SWORD_MINING_SPEED = 1.5f;
    public static final float UNMINABLE = Float.NEGATIVE_INFINITY;

    private static final ItemEvalRegistry<Block, Float> REGISTRY = new ItemEvalRegistry<>(UNMINABLE) {};

    static {
        // Hand provider
        register(ItemEvalRegistry.PRIORITY_LOW, (_stack, block) -> Optional.of(
                block.material.isHandHarvestable() ? STANDARD_MINING_SPEED : UNMINABLE
        ));

        // Vanilla provider
        register(ItemEvalRegistry.PRIORITY_VANILLA, (stack, block) -> {
            if (stack == null) return Optional.empty();
            boolean isSuitable = block.material.isHandHarvestable() || stack.isSuitableFor(block);
            return Optional.of(isSuitable ? stack.getMiningSpeedMultiplier(block) : UNMINABLE);
        });

        // Sword provider
        register(ItemEvalRegistry.PRIORITY_HIGH, (stack, block) -> {
            if (stack != null && stack.getItem() instanceof SwordItem) {
                if (!CONFIG.useSwordForMining) return Optional.of(UNMINABLE);
                return block.material.isHandHarvestable()
                        ? Optional.of(STANDARD_SWORD_MINING_SPEED)
                        : Optional.empty();
            }
            return Optional.empty();
        });
    }

    private MiningSpeedRegistry() {}

    public static void register(@NotNull ItemEvalProvider<Block, Float> provider) {
        REGISTRY.register(provider);
    }

    public static void register(int priority, @NotNull ItemEvalProvider<Block, Float> provider) {
        REGISTRY.register(priority, provider);
        LOGGER.info("Mining speed provider registered with priority %d.", priority);
    }

    public static float getSpeed(@Nullable ItemStack stack, @NotNull Block block) {
        return REGISTRY.evaluate(stack, block);
    }
}