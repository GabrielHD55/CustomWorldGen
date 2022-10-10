package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

public enum GrassColorModifier {

    NONE(BiomeSpecialEffects.GrassColorModifier.NONE),
    DARK_FOREST(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST),
    SWAMP(BiomeSpecialEffects.GrassColorModifier.SWAMP);

    private final BiomeSpecialEffects.GrassColorModifier modifier;

    GrassColorModifier(BiomeSpecialEffects.GrassColorModifier modi) {
        this.modifier = modi;
    }

    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.modifier;
    }
}
