package com.gabrielhd.worldgen.builder;

import com.gabrielhd.worldgen.biome.types.VeinType;
import com.gabrielhd.worldgen.utils.NoiseUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.Stream;

import static net.minecraft.world.level.levelgen.NoiseRouterData.*;

public class NoiseRouterBuilder {

    private static final ResourceKey<DensityFunction> Y = createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");

    public static final ResourceKey<NormalNoise.NoiseParameters> HUMIDITY = createNoise("humidity");
    public static final ResourceKey<NormalNoise.NoiseParameters> WEIRDNESS = createNoise("weirdness");

    private static ResourceKey<NormalNoise.NoiseParameters> createNoise(String var0) {
        return ResourceKey.create(Registry.NOISE_REGISTRY, new ResourceLocation(var0));
    }

    private static ResourceKey<DensityFunction> createKey(String var0) {
        return ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation(var0));
    }

    public static NoiseRouter overworld(Registry<DensityFunction> var0) {
        DensityFunction barrier = DensityFunctions.noise(NoiseUtils.getNoise(Noises.AQUIFER_BARRIER), 0);
        DensityFunction fluid_level_floodedness = DensityFunctions.noise(NoiseUtils.getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0);
        DensityFunction fluid_level_spread = DensityFunctions.noise(NoiseUtils.getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0);
        DensityFunction lava = DensityFunctions.noise(NoiseUtils.getNoise(Noises.AQUIFER_LAVA));
        DensityFunction shift_x = NoiseUtils.getFunction(var0, SHIFT_X);
        DensityFunction shift_z = NoiseUtils.getFunction(var0, SHIFT_Z);
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(shift_x, shift_z, 0.25D, NoiseUtils.getNoise(Noises.TEMPERATURE));
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(shift_x, shift_z, 0.25D, NoiseUtils.getNoise(Noises.VEGETATION));
        DensityFunction continents = NoiseUtils.getFunction(var0, CONTINENTS);
        DensityFunction erosion = NoiseUtils.getFunction(var0, EROSION);
        DensityFunction depth = NoiseUtils.getFunction(var0, NoiseRouterData.DEPTH);
        DensityFunction factor = NoiseUtils.getFunction(var0, NoiseRouterData.FACTOR);
        DensityFunction ridges = NoiseUtils.getFunction(var0, RIDGES);
        DensityFunction var13 = noiseGradientDensity(DensityFunctions.cache2d(factor), depth);
        DensityFunction var14 = NoiseUtils.getFunction(var0, SLOPED_CHEESE);
        DensityFunction var15 = DensityFunctions.min(var14, DensityFunctions.mul(DensityFunctions.constant(5.0D), NoiseUtils.getFunction(var0, ENTRANCES)));
        DensityFunction var16 = DensityFunctions.rangeChoice(var14, -1000000.0D, 1.5625D, var15, underground(var0, var14));
        DensityFunction var17 = DensityFunctions.min(postProcess(slideOverworld(false, var16)), NoiseUtils.getFunction(var0, NOODLE));
        DensityFunction var18 = NoiseUtils.getFunction(var0, Y);
        int var19 = Stream.of(VeinType.values()).mapToInt(VeinType::getMinY).min().orElse(-DimensionType.MIN_Y * 2);
        int var20 = Stream.of(VeinType.values()).mapToInt(VeinType::getMaxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction var21 = yLimitedInterpolatable(var18, DensityFunctions.noise(NoiseUtils.getNoise(Noises.ORE_VEININESS), 1.5D, 1.5D), var19, var20, 0);
        float var22 = 4.0F;
        DensityFunction var23 = yLimitedInterpolatable(var18, DensityFunctions.noise(NoiseUtils.getNoise(Noises.ORE_VEIN_A), 4.0D, 4.0D), var19, var20, 0).abs();
        DensityFunction var24 = yLimitedInterpolatable(var18, DensityFunctions.noise(NoiseUtils.getNoise(Noises.ORE_VEIN_B), 4.0D, 4.0D), var19, var20, 0).abs();
        DensityFunction var25 = DensityFunctions.add(DensityFunctions.constant(-0.07999999821186066D), DensityFunctions.max(var23, var24));
        DensityFunction var26 = DensityFunctions.noise(NoiseUtils.getNoise(Noises.ORE_GAP));
        return new NoiseRouter(barrier, fluid_level_floodedness, fluid_level_spread, lava, temperature, vegetation, continents, erosion, depth, ridges, slideOverworld(false, DensityFunctions.add(var13, DensityFunctions.constant(-0.703125D)).clamp(-64.0D, 64.0D)), var17, var21, var25, var26);
    }

    public static DensityFunction getTemperature() {
        Registry<DensityFunction> iregistry = BuiltinRegistries.DENSITY_FUNCTION;

        DensityFunction shift_x = NoiseUtils.getFunction(iregistry, SHIFT_X);
        DensityFunction shift_z = NoiseUtils.getFunction(iregistry, SHIFT_Z);

        return DensityFunctions.shiftedNoise2d(shift_x, shift_z, 0.25D, NoiseUtils.getNoise(Noises.TEMPERATURE));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction var0, DensityFunction var1) {
        DensityFunction var2 = DensityFunctions.mul(var1, var0);
        return DensityFunctions.mul(DensityFunctions.constant(4.0D), var2.quarterNegative());
    }

    private static DensityFunction underground(Registry<DensityFunction> var0, DensityFunction var1) {
        DensityFunction var2 = NoiseUtils.getFunction(var0, SPAGHETTI_2D);
        DensityFunction var3 = NoiseUtils.getFunction(var0, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction var4 = DensityFunctions.noise(NoiseUtils.getNoise(Noises.CAVE_LAYER), 8.0D);
        DensityFunction var5 = DensityFunctions.mul(DensityFunctions.constant(4.0D), var4.square());
        DensityFunction var6 = DensityFunctions.noise(NoiseUtils.getNoise(Noises.CAVE_CHEESE), 0.6666666666666666D);
        DensityFunction var7 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27D), var6).clamp(-1.0D, 1.0D), DensityFunctions.add(DensityFunctions.constant(1.5D), DensityFunctions.mul(DensityFunctions.constant(-0.64D), var1)).clamp(0.0D, 0.5D));
        DensityFunction var8 = DensityFunctions.add(var5, var7);
        DensityFunction var9 = DensityFunctions.min(DensityFunctions.min(var8, NoiseUtils.getFunction(var0, ENTRANCES)), DensityFunctions.add(var2, var3));
        DensityFunction var10 = NoiseUtils.getFunction(var0, PILLARS);
        DensityFunction var11 = DensityFunctions.rangeChoice(var10, -1000000.0D, 0.03D, DensityFunctions.constant(-1000000.0D), var10);
        return DensityFunctions.max(var9, var11);
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction var0, DensityFunction var1, int var2, int var3, int var4) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(var0, var2, var3 + 1, var1, DensityFunctions.constant(var4)));
    }

    private static DensityFunction postProcess(DensityFunction var0) {
        DensityFunction var1 = DensityFunctions.blendDensity(var0);
        return DensityFunctions.mul(DensityFunctions.interpolated(var1), DensityFunctions.constant(0.64D)).squeeze();
    }


    private static DensityFunction slideOverworld(boolean var0, DensityFunction var1) {
        return slide(var1, -64, 384, var0 ? 16 : 80, var0 ? 0 : 64, -0.078125D, 0, 24, var0 ? 0.4D : 0.1171875D);
    }

    private static DensityFunction slide(DensityFunction var0, int var1, int var2, int var3, int var4, double var5, int var7, int var8, double var9) {
        DensityFunction var12 = DensityFunctions.yClampedGradient(var1 + var2 - var3, var1 + var2 - var4, 1.0D, 0.0D);
        DensityFunction var11 = DensityFunctions.lerp(var12, var5, var0);
        DensityFunction var13 = DensityFunctions.yClampedGradient(var1 + var7, var1 + var8, 0.0D, 1.0D);
        var11 = DensityFunctions.lerp(var13, var9, var11);
        return var11;
    }
}
