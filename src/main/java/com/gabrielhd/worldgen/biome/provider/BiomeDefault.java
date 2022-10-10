package com.gabrielhd.worldgen.biome.provider;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import com.gabrielhd.worldgen.builder.NoiseRouterBuilder;
import com.gabrielhd.worldgen.utils.NoiseUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.world.level.levelgen.NoiseRouterData.DEPTH;
import static net.minecraft.world.level.levelgen.NoiseRouterData.EROSION;

public abstract class BiomeDefault extends CustomBiomeProvider {

    public static class Builder{
        private final Function<org.bukkit.block.Biome, org.bukkit.block.Biome> biomeReplaceFunction;
        private final PresetBiomes presetBiomes;

        public Builder(Function<org.bukkit.block.Biome, org.bukkit.block.Biome> biomeReplaceFunction, PresetBiomes presetBiomes){
            this.biomeReplaceFunction = biomeReplaceFunction;
            this.presetBiomes = presetBiomes;
        }

        public BiomeDefault create(){
            WritableRegistry<Biome> registry = (WritableRegistry<Biome>) ((CraftServer)Bukkit.getServer()).getServer().registryHolder.ownedRegistryOrThrow(Registry.BIOME_REGISTRY);

            return new BiomeDefault() {
                private final Function<org.bukkit.block.Biome, org.bukkit.block.Biome> biomeReplaceFunction = Builder.this.biomeReplaceFunction;
                private Sampler noiseSampler = null;
                private final BiomeSource biomeSource = presetBiomes == PresetBiomes.OVERWORLD ? MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(registry,true) : MultiNoiseBiomeSource.Preset.NETHER.biomeSource(registry,true);

                @Override
                public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
                    if(noiseSampler == null){
                        Registry<DensityFunction> iregistry = BuiltinRegistries.DENSITY_FUNCTION;

                        this.noiseSampler = new Climate.Sampler(NoiseRouterBuilder.getTemperature(), DensityFunctions.noise(NoiseUtils.getNoise(NoiseRouterBuilder.HUMIDITY), Math.random()), DensityFunctions.noise(NoiseUtils.getNoise(Noises.CONTINENTALNESS), Math.random()), NoiseUtils.getFunction(iregistry, EROSION), NoiseUtils.getFunction(iregistry, DEPTH), DensityFunctions.noise(NoiseUtils.getNoise(NoiseRouterBuilder.WEIRDNESS), Math.random()), new ArrayList<>());
                    }

                    Biome vanilla = biomeSource.getNoiseBiome(x >> 2,y>>2,z>>2,noiseSampler).value();

                    if(biomeReplaceFunction != null){
                        org.bukkit.block.Biome bukkitchangebiome = biomeReplaceFunction.apply(CraftBlock.biomeBaseToBiome(registry, vanilla));

                        if(bukkitchangebiome != null){
                            vanilla = CraftBlock.biomeToBiomeBase(registry, bukkitchangebiome).value();
                        }
                    }

                    return vanilla;
                }

                @Override
                public List<Object> getBiomes(WorldInfo worldInfo) {
                    List<Object> biomes = new ArrayList<>();
                    for(org.bukkit.block.Biome biome : org.bukkit.block.Biome.values()) {
                        if(biome != org.bukkit.block.Biome.CUSTOM){
                            biomes.add(biome);
                        }
                    }
                    return biomes;
                }
            };
        }
    }


    public enum PresetBiomes{
        OVERWORLD,
        NETHER
    }
}
