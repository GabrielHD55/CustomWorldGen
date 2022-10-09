package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

public enum Carvers {

    CAVE(net.minecraft.data.worldgen.Carvers.CAVE.value()),
    CAVE_EXTRA_UNDERGROUND(net.minecraft.data.worldgen.Carvers.CAVE_EXTRA_UNDERGROUND.value()),
    CANYON(net.minecraft.data.worldgen.Carvers.CANYON.value()),
    NETHER_CAVE(net.minecraft.data.worldgen.Carvers.NETHER_CAVE.value());

    private final ConfiguredWorldCarver<?> carver;

    Carvers(ConfiguredWorldCarver<?> b) {
        carver = b;
    }

    protected ConfiguredWorldCarver<?> getCarver(){
        return carver;
    }
}
