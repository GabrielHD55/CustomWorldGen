package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.biome.Biome;

public enum Precipitation {

    NONE(Biome.Precipitation.NONE),
    RAIN(Biome.Precipitation.RAIN),
    SNOW(Biome.Precipitation.SNOW);

    private final Biome.Precipitation precipitation;

    Precipitation(Biome.Precipitation precipitation){
        this.precipitation = precipitation;
    }

    public Biome.Precipitation getPrecipitation(){
        return this.precipitation;
    }
}
