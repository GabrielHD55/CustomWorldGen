package com.gabrielhd.worldgen;

import com.gabrielhd.worldgen.Builder.EnvironmentBuilder;
import com.gabrielhd.worldgen.Builder.GeneratorConfiguration;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class CustomWorldCreator extends WorldCreator {

    private EnvironmentBuilder environmentBuilder = null;
    private GeneratorConfiguration generatorConfiguration = null;

    public CustomWorldCreator(String name) {
        super(name);
    }

    public void setGeneratorConfiguration(GeneratorConfiguration generatorConfiguration) {
        this.generatorConfiguration = generatorConfiguration;
    }

    public GeneratorConfiguration getGeneratorConfiguration() {
        return generatorConfiguration;
    }

    public void setEnvironmentBuilder(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
        if (environmentBuilder == null) {
            environment(World.Environment.NORMAL);
        } else {
            environment(World.Environment.CUSTOM);
        }
    }

    public EnvironmentBuilder getEnvironmentBuilder() {
        return environmentBuilder;
    }

    @Override
    public World createWorld() {
        return CustomWorldGen.createWorld(this);
    }
}
