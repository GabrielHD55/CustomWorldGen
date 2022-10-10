package com.gabrielhd.worldgen.biome.provider;

import com.gabrielhd.worldgen.biome.BiomeCreator;
import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import com.gabrielhd.worldgen.builder.NoiseRouterBuilder;
import com.gabrielhd.worldgen.utils.NoiseUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.generator.WorldInfo;

import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.world.level.levelgen.NoiseRouterData.DEPTH;
import static net.minecraft.world.level.levelgen.NoiseRouterData.EROSION;

public abstract class BiomeMultiNoise extends CustomBiomeProvider {

    private final HashMap<Object,NoiseData> noiseDatas = new HashMap<>();

    public BiomeMultiNoise create(){
        WritableRegistry<Biome> registry = (WritableRegistry<Biome>) ((CraftServer)Bukkit.getServer()).getServer().registryHolder.ownedRegistryOrThrow(Registry.BIOME_REGISTRY);
        ImmutableList.Builder<Pair<Climate.ParameterPoint, Supplier<Biome>>> pairBuilder = ImmutableList.builder();

        for(Object biomeObj : noiseDatas.keySet()){
            NoiseData nD = noiseDatas.get(biomeObj);

            Climate.ParameterPoint parameterPoint = Climate.parameters(nD.temperature, nD.humidity, nD.continentalness,
                    nD.erosion, nD.depth, nD.weirdness, nD.offset);
            Biome nmsBiome = null;

            if(biomeObj instanceof org.bukkit.block.Biome biome) {
                Preconditions.checkArgument(biome != org.bukkit.block.Biome.CUSTOM, "Cannot use the biome %s", biome);
                nmsBiome = CraftBlock.biomeToBiomeBase(registry, biome).value();
            }else if(biomeObj instanceof BiomeCreator.CustomBiome customBiome){
                nmsBiome = customBiome.getBiome();
            }

            final Biome finalNmsBiome = nmsBiome;
            pairBuilder.add(Pair.of(parameterPoint, () -> finalNmsBiome));
        }

        Climate.ParameterList<Supplier<Biome>> parameterList = new Climate.ParameterList(pairBuilder.build());

        return new BiomeMultiNoise() {
            private Climate.Sampler noiseSampler = null;
            private final BiomeSource biomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(registry,true);


            private Supplier<Biome> getNoiseBiome(Climate.TargetPoint targetPoint) {
                return parameterList.findValue(targetPoint);
            }

            private String getNMSBiomeName(Biome biome) {
                Optional<ResourceKey<Biome>> optional = BuiltinRegistries.BIOME.getResourceKey(biome);

                if(optional.isPresent()){
                    return optional.get().location().toString();
                }
                CraftServer craftServer = (CraftServer) Bukkit.getServer();
                RegistryAccess registryAccess = craftServer.getServer().registryAccess();
                WritableRegistry<Biome> biomeRegistry = (WritableRegistry<Biome>) registryAccess.ownedRegistryOrThrow(Registry.BIOME_REGISTRY);

                ResourceLocation location = biomeRegistry.getKey(biome);
                if(location != null){
                    return location.toString();
                }

                return null;
            }

            @Override
            public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
                if(noiseSampler == null){
                    Registry<DensityFunction> iregistry = BuiltinRegistries.DENSITY_FUNCTION;

                    this.noiseSampler = new Climate.Sampler(NoiseRouterBuilder.getTemperature(), DensityFunctions.noise(NoiseUtils.getNoise(NoiseRouterBuilder.HUMIDITY), Math.random()), DensityFunctions.noise(NoiseUtils.getNoise(Noises.CONTINENTALNESS), Math.random()), NoiseUtils.getFunction(iregistry, EROSION), NoiseUtils.getFunction(iregistry, DEPTH), DensityFunctions.noise(NoiseUtils.getNoise(NoiseRouterBuilder.WEIRDNESS), Math.random()), new ArrayList<>());
                }

                return getNoiseBiome(noiseSampler.sample(x,y,z)).get();
            }

            @Override
            public List<Object> getBiomes(WorldInfo worldInfo) {
                return new ArrayList<>(noiseDatas.keySet());
            }
        };
    }

    public Set<Object> getBiomes(){
        return noiseDatas.keySet();
    }

    public HashMap<Object,NoiseData> getBiomeNoiseData(){
        return noiseDatas;
    }

    public BiomeMultiNoise addBiome(Object biome, NoiseData noiseData){
        noiseDatas.put(biome, noiseData);
        return this;
    }

    @Getter
    public static class NoiseData{
        final float temperature;
        final float humidity;
        final float continentalness;
        final float erosion;
        final float depth;
        final float weirdness;
        final float offset;

        public NoiseData(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset){
            this.temperature = temperature;
            this.humidity = humidity;
            this.continentalness = continentalness;
            this.erosion = erosion;
            this.depth = depth;
            this.weirdness = weirdness;
            this.offset = offset;
        }
    }
}
