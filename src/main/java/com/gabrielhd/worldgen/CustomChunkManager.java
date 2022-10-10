package com.gabrielhd.worldgen;

import com.gabrielhd.worldgen.biome.BiomeCreator;
import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class CustomChunkManager extends BiomeSource {

    private final WorldInfo worldInfo;
    private final CustomBiomeProvider biomeProvider;
    private final Registry<Biome> registry;

    private static List<Holder<Biome>> biomeListToBiomeBaseList(List<Object> biomes, Registry<Biome> registry) {
        List<Holder<Biome>> biomeBases = new ArrayList();

        for (Object next : biomes) {
            if (next instanceof org.bukkit.block.Biome biome) {
                Preconditions.checkArgument(biome != org.bukkit.block.Biome.CUSTOM, "Cannot use the biome %s", biome);
                Biome biomebase = CraftBlock.biomeToBiomeBase(registry, biome).value();

                biomeBases.add(Holder.direct(biomebase));
            } else if (next instanceof BiomeCreator.CustomBiome customBiome) {
                biomeBases.add(Holder.direct(customBiome.getBiome()));
            } else {
                biomeBases.add(Holder.direct((Biome) next));
            }
        }

        return biomeBases;
    }

    public CustomChunkManager(WorldInfo worldInfo, CustomBiomeProvider biomeProvider, Registry<Biome> registry) {
        super(biomeListToBiomeBaseList(biomeProvider.getBiomes(worldInfo), registry));

        this.worldInfo = worldInfo;
        this.biomeProvider = biomeProvider;
        this.registry = registry;
    }

    protected Codec<? extends BiomeSource> codec() {
        throw new UnsupportedOperationException("Cannot serialize CustomChunkManager");
    }

    public BiomeSource withSeed(long l) {
        throw new UnsupportedOperationException("Cannot copy CustomChunkManager");
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        Object biomeObj = this.biomeProvider.getBiome(this.worldInfo, x << 2, y << 2, z << 2);
        if(biomeObj instanceof org.bukkit.block.Biome biome) {
            Preconditions.checkArgument(biome != org.bukkit.block.Biome.CUSTOM, "Cannot set the biome to %s", biome);

            return CraftBlock.biomeToBiomeBase(this.registry, biome);
        } else if(biomeObj instanceof BiomeCreator.CustomBiome customBiome) {
            return Holder.direct(customBiome.getBiome());
        }

        return Holder.direct((Biome) biomeObj);
    }
}
