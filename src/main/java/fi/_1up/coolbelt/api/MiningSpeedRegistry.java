package fi._1up.coolbelt.api;

import fi._1up.coolbelt.impl.DurabilityChecker;
import fi._1up.coolbelt.impl.compat.stationapi.StationAPICompat;
import fi._1up.coolbelt.impl.compat.stationapi.StAPIBlockSmuggler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;
import static fi._1up.coolbelt.config.RootConfig.CONFIG;
import static fi._1up.coolbelt.api.ItemEvalRegistry.*;

/// Registry managing mining speed evaluations for [ItemStack] instances against target [Block] instances.
/// Coolbelt will generally prefer the highest mining speed out of the evaluated providers.
@ApiStatus.Experimental
public final class MiningSpeedRegistry {
    /// Default mining speed multiplier for hand harvesting.
    public static final float STANDARD_MINING_SPEED = 1.0f;
    /// Default mining speed multiplier for sword harvesting on hand-harvestable blocks.
    public static final float STANDARD_SWORD_MINING_SPEED = 1.5f;
    /// Sentinel value indicating a block cannot be mined by the item stack.
    public static final float UNMINABLE = Float.NEGATIVE_INFINITY;

    /// Internal [ItemEvalRegistry] instance mapping [ItemStack] and [Object] references to mining speed multipliers.
    private static final ItemEvalRegistry<Object, Float> REGISTRY = new ItemEvalRegistry<>(UNMINABLE) {};

    static {
        // Hand provider
        register(PRIORITY_LOW, (_stack, block) -> Optional.of(
            block.material.isHandHarvestable() ? STANDARD_MINING_SPEED : UNMINABLE
        ));

        // Vanilla provider
        register(PRIORITY_VANILLA, (stack, block) -> {
            if (stack == null) return Optional.empty();
            boolean isSuitable = block.material.isHandHarvestable() || stack.isSuitableFor(block);
            return Optional.of(isSuitable ? stack.getMiningSpeedMultiplier(block) : UNMINABLE);
        });

        // Sword provider
        register(PRIORITY_HIGH, (stack, block) -> {
            if (stack != null && stack.getItem() instanceof SwordItem) {
                if (!CONFIG.useSwordForMining) return Optional.of(UNMINABLE);
                return block.material.isHandHarvestable()
                    ? Optional.of(STANDARD_SWORD_MINING_SPEED)
                    : Optional.empty();
            }
            return Optional.empty();
        });

        // Durability saver
        register(PRIORITY_CRITICAL, (stack, block) -> {
            if (CONFIG.leaveOneDurability && DurabilityChecker.isAtOrBelow(stack, 1)) {
                return Optional.of(UNMINABLE);
            }
            return Optional.empty();
        });
    }

    /// Private constructor to prevent instantiation.
    private MiningSpeedRegistry() {}

    /// Registers an [ItemEvalProvider] using default [ItemEvalRegistry#PRIORITY_DEFAULT].
    ///
    /// @param provider [ItemEvalProvider] implementation to add.
    @SuppressWarnings("unused")
    public static void register(@NotNull ItemEvalProvider<Block, Float> provider) {
        register(PRIORITY_DEFAULT, provider);
    }

    /// Registers an [ItemEvalProvider] with a specified priority.
    ///
    /// @param priority Numerical priority determining execution order.
    /// @param provider [ItemEvalProvider] implementation to add.
    public static void register(int priority, @NotNull ItemEvalProvider<Block, Float> provider) {
        registerInternal(priority, (stack, object)->{
            Block block = toBlock(object);
            if (block == null) return Optional.of(UNMINABLE);
            return provider.get(stack, block);
        });
    }

    /// Registers an [ItemEvalProvider] with a specified priority.
    ///
    /// @param priority Numerical priority determining execution order.
    /// @param provider [ItemEvalProvider] implementation to add.
    @ApiStatus.Internal
    public static void registerInternal(int priority, @NotNull ItemEvalProvider<Object, Float> provider) {
        REGISTRY.register(priority, provider);
        LOGGER.info("Mining speed provider registered with priority %d.", priority);
    }

    private static Block toBlock(Object object) {
        if(StationAPICompat.IS_STAPI_LOADED) return StationAPICompat.toBlock(object);
        if(object instanceof Block block) return block;
        return null;
    }

    /// Evaluates the mining speed multiplier for an [ItemStack] against a target [Block] or [StAPIBlockSmuggler].
    /// Coolbelt will generally prefer the highest mining speed out of the evaluated providers.
    ///
    /// @param stack [ItemStack] being evaluated.
    /// @param block Target [Block] or [StAPIBlockSmuggler].
    /// @return Mining speed multiplier returned by the highest priority provider, or [UNMINABLE] if none match.
    public static float getSpeed(@Nullable ItemStack stack, @NotNull Object block) {
        return REGISTRY.evaluate(stack, block);
    }
}
