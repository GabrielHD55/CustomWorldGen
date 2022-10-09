package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.levelgen.GenerationStep;

public enum FeatureType {

    AIR(GenerationStep.Carving.AIR),
    LIQUID(GenerationStep.Carving.LIQUID);

    private final GenerationStep.Carving feature;

    FeatureType(GenerationStep.Carving a) {
        this.feature = a;
    }

    protected GenerationStep.Carving getFeature(){
        return feature;
    }

}
