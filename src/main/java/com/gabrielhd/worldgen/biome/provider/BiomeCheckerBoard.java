package com.gabrielhd.worldgen.biome.provider;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import lombok.Getter;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class BiomeCheckerBoard extends CustomBiomeProvider {

    @Getter
    public static class Builder{
        private int size;
        private final List<Object> biomes = new ArrayList<>();

        public Builder(int gridsize){
            this.size = gridsize;
        }

        public BiomeCheckerBoard create(){
            return new BiomeCheckerBoard() {

                private final int sizeThis = size;
                private final List<Object> biomesThis = biomes;

                @Override
                public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
                    return biomesThis.get(Math.floorMod((x >> this.sizeThis) + (z >> this.sizeThis), this.biomesThis.size()));
                }

                @Override
                public List<Object> getBiomes(WorldInfo worldInfo) {
                    return biomesThis;
                }
            };
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder addBiome(Object biome){
            biomes.add(biome);
            return this;
        }
    }
}
