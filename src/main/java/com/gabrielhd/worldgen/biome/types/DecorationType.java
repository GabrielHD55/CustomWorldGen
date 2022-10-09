package com.gabrielhd.worldgen.biome.types;

import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.HashMap;

public enum DecorationType {

    RAW_GENERATION(GenerationStep.Decoration.RAW_GENERATION),
    LAKES(GenerationStep.Decoration.LAKES),
    LOCAL_MODIFICATIONS(GenerationStep.Decoration.LOCAL_MODIFICATIONS),
    UNDERGROUND_STRUCTURES(GenerationStep.Decoration.UNDERGROUND_STRUCTURES),
    SURFACE_STRUCTURES(GenerationStep.Decoration.SURFACE_STRUCTURES),
    STRONGHOLDS(GenerationStep.Decoration.STRONGHOLDS),
    UNDERGROUND_ORES(GenerationStep.Decoration.UNDERGROUND_ORES),
    UNDERGROUND_DECORATION(GenerationStep.Decoration.UNDERGROUND_DECORATION),
    FLUID_SPRINGS(GenerationStep.Decoration.FLUID_SPRINGS),
    VEGETAL_DECORATION(GenerationStep.Decoration.VEGETAL_DECORATION),
    TOP_LAYER_MODIFICATION(GenerationStep.Decoration.TOP_LAYER_MODIFICATION);

    private static final HashMap<GenerationStep.Decoration, DecorationType> valuemap = new HashMap<>();
    private final GenerationStep.Decoration deco;

    DecorationType(GenerationStep.Decoration deco){
        this.deco = deco;
    }

    protected GenerationStep.Decoration get(){
        return deco;
    }

    protected static DecorationType fromNMSDecoration(GenerationStep.Decoration decoration) {
        return valuemap.get(decoration);
    }

    static {
        for(DecorationType deco : DecorationType.values()) {
            valuemap.put(deco.get(), deco);
        }
    }
}
