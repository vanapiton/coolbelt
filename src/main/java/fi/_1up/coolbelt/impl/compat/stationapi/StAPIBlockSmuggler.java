package fi._1up.coolbelt.impl.compat.stationapi;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;

public record StAPIBlockSmuggler(BlockState state, BlockView blockView, BlockPos pos) {}
