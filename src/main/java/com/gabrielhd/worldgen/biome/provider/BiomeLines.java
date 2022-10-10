package com.gabrielhd.worldgen.biome.provider;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import lombok.Getter;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class BiomeLines extends CustomBiomeProvider {

    @Getter
    public static class Builder {
        private int size;
        private boolean xDir;
        private final List<Object> biomes = new ArrayList<>();

        public Builder(int size, boolean xDir){
            this.size = size;
            this.xDir = xDir;
        }

        public BiomeLines create(){
            return new BiomeLines() {
                private final int sizeThis = size;
                private final boolean xDirThis = xDir;
                private final List<Object> biomesThis = biomes;

                @Override
                public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
                    int dir = x;

                    if(!xDir) {
                        dir = z;
                    }

                    return biomesThis.get(Math.floorMod((dir >> this.sizeThis), this.biomesThis.size()));
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

        public Builder setxDir(boolean xDir) {
            this.xDir = xDir;
            return this;
        }

        public Builder addBiome(Object biome){
            biomes.add(biome);
            return this;
        }
    }
}
