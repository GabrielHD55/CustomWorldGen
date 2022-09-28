package com.gabrielhd.worldgen;

import com.gabrielhd.worldgen.Builder.EnvironmentBuilder;
import com.gabrielhd.worldgen.Builder.GeneratorConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomWorldGen extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveResource("aether.json", false);

        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder();
        environmentBuilder.setNatural(true);
        environmentBuilder.setPiglinSafe(false);
        environmentBuilder.setRespawnAnchorWorks(false);
        environmentBuilder.setBedWorks(true);
        environmentBuilder.setHasRaids(true);
        environmentBuilder.setHasSkylight(true);
        environmentBuilder.setHasCeiling(false);
        environmentBuilder.setCoordinateScale(1);
        environmentBuilder.setAmbientLight(0f);
        environmentBuilder.setLogicalHeight(256);
        environmentBuilder.setMinY(-512);
        environmentBuilder.setHeight(1024);

        GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration();
        generatorConfiguration.setSeaLevel(-512);
        generatorConfiguration.setDisableMobGeneration(false);
        generatorConfiguration.setAquifersEnabled(false);
        generatorConfiguration.setOreVeinsEnabled(false);
        generatorConfiguration.setLegacyRandomSource(true);
        generatorConfiguration.setDefaultBlock(Material.STONE);
        generatorConfiguration.setDefaultFluid(Material.WATER);

        CustomWorldCreator customWorldCreator = new CustomWorldCreator("test");
        customWorldCreator.setEnvironmentBuilder(environmentBuilder);
        customWorldCreator.setGeneratorConfiguration(generatorConfiguration);

        try {
            customWorldCreator.generatorSettings(new String(Files.readAllBytes(Paths.get(new File(this.getDataFolder(), "aether.json").toURI()))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getServer().getScheduler().runTaskLater(this, customWorldCreator::createWorld, 200L);
    }
}
