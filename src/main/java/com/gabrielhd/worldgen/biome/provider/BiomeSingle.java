package com.gabrielhd.worldgen.biome.provider;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import lombok.Getter;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;


public class BiomeSingle extends CustomBiomeProvider {

    @Override
    public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return null;
    }

    @Override
    public List<Object> getBiomes(WorldInfo worldInfo) {
        return null;
    }

    @Getter
    public static class Builder{

        private final Object biome;

        public Builder(Object biome){
            this.biome = biome;
        }

        public BiomeSingle create(){

            return new BiomeSingle() {
                private final Object biomesThis = biome;

                @Override
                public Object getBiome(WorldInfo worldInfo, int x, int y, int z) {
                   return biomesThis;
                }

                @Override
                public List<Object> getBiomes(WorldInfo worldInfo) {
                    return Collections.singletonList(biomesThis);
                }
            };
        }
    }
}
