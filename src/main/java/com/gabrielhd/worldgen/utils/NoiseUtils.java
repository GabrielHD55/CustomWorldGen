package com.gabrielhd.worldgen.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseUtils {

    public static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> resourcekey) {
        return BuiltinRegistries.NOISE.getHolderOrThrow(resourcekey);
    }

    public static DensityFunction getFunction(Registry<DensityFunction> var0, ResourceKey<DensityFunction> var1) {
        return new DensityFunctions.HolderHolder(var0.getHolderOrThrow(var1));
    }
}
