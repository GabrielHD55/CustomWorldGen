package com.gabrielhd.worldgen.biome;

import org.bukkit.generator.WorldInfo;

import java.util.List;

public abstract class CustomBiomeProvider {

    public abstract Object getBiome(WorldInfo worldInfo, int x, int y, int z);

    public abstract List<Object> getBiomes(WorldInfo worldInfo);

}
