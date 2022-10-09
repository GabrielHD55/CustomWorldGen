package com.gabrielhd.worldgen.biome.methods;

import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public class BiomeEntity {

    private final EntityType type;
    private final int weight;
    private final int mincount;
    private final int maxcount;

    public BiomeEntity(EntityType type, int weight, int mincount, int maxcount){
        this.type = type;
        this.weight = weight;
        this.mincount = mincount;
        this.maxcount = maxcount;

    }
}
