package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.biome.Biome;

public enum TemperatureModifier {

    NONE(Biome.TemperatureModifier.NONE),
    FROZEN(Biome.TemperatureModifier.FROZEN);

    private final Biome.TemperatureModifier modifier;

    TemperatureModifier(Biome.TemperatureModifier modifier){
        this.modifier = modifier;
    }

    public Biome.TemperatureModifier getTemperaturModifier(){
        return this.modifier;
    }
}
