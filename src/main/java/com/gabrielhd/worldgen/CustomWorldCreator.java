package com.gabrielhd.worldgen;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import com.gabrielhd.worldgen.builder.EnvironmentBuilder;
import com.gabrielhd.worldgen.builder.GeneratorConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.WorldCreator;


@Getter @Setter
public class CustomWorldCreator extends WorldCreator {

    private EnvironmentBuilder environmentBuilder = null;
    private CustomBiomeProvider customBiomeProvider = null;
    private GeneratorConfiguration generatorConfiguration = null;

    public CustomWorldCreator(String name) {
        super(name);
    }

    public void setEnvironmentBuilder(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
        if (environmentBuilder == null) {
            environment(World.Environment.NORMAL);
        } else {
            environment(World.Environment.CUSTOM);
        }
    }

    @Override
    public World createWorld() {
        return CustomWorldGenAPI.createWorld(this);
    }
}
