package com.gabrielhd.worldgen.builder;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

@Getter
public class EnvironmentBuilder {

    private Long fixedTime = null;
    private boolean hasSkylight = true;
    private boolean hasCeiling = true;
    private boolean ultraWarm = false;
    private boolean natural = true;
    private double coordinateScale = 1.0;
    private boolean createDragonFight = false;
    private boolean piglinSafe = false;
    private boolean bedWorks = true;
    private boolean respawnAnchorWorks = false;
    private boolean hasRaids = true;
    private int minY = -64;
    private int height = 384;
    private int logicalHeight = 384;
    private NamespacedKey infiniburn = Tag.INFINIBURN_OVERWORLD.getKey();
    private NamespacedKey effectsLocation = NamespacedKey.minecraft("overworld");
    private float ambientLight = 0.0f;

    public EnvironmentBuilder() {
    }

    public EnvironmentBuilder(Long fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultraWarm, boolean natural, double coordinateScale, boolean createDragonFight, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int minY, int height, int logicalHeight, NamespacedKey infiniburn, NamespacedKey effectsLocation, float ambientLight) {
        this.fixedTime = fixedTime;
        this.hasSkylight = hasSkylight;
        this.hasCeiling = hasCeiling;
        this.ultraWarm = ultraWarm;
        this.natural = natural;
        this.coordinateScale = coordinateScale;
        this.createDragonFight = createDragonFight;
        this.piglinSafe = piglinSafe;

        this.bedWorks = bedWorks;
        this.respawnAnchorWorks = respawnAnchorWorks;
        this.hasRaids = hasRaids;
        this.minY = minY;
        this.height = height;
        this.logicalHeight = logicalHeight;
        this.infiniburn = infiniburn;
        this.effectsLocation = effectsLocation;
        this.ambientLight = ambientLight;
    }

    public EnvironmentBuilder setAmbientLight(Float ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public EnvironmentBuilder setBedWorks(boolean bedWorks) {
        this.bedWorks = bedWorks;
        return this;
    }

    public EnvironmentBuilder setCoordinateScale(double coordinateScale) {
        this.coordinateScale = coordinateScale;
        return this;
    }

    public EnvironmentBuilder setCreateDragonFight(boolean createDragonFight) {
        this.createDragonFight = createDragonFight;
        return this;
    }

    public EnvironmentBuilder setEffectsLocation(NamespacedKey effectsLocation) {
        this.effectsLocation = effectsLocation;
        return this;
    }

    public EnvironmentBuilder setFixedTime(Long fixedTime) {
        this.fixedTime = fixedTime;
        return this;
    }

    public EnvironmentBuilder setHasCeiling(boolean hasCeiling) {
        this.hasCeiling = hasCeiling;
        return this;
    }

    public EnvironmentBuilder setHasRaids(boolean hasRaids) {
        this.hasRaids = hasRaids;
        return this;
    }

    public EnvironmentBuilder setHasSkylight(boolean hasSkylight) {
        this.hasSkylight = hasSkylight;
        return this;
    }

    public EnvironmentBuilder setHeight(int height) {
        if (height > 4064 || height < 16 || height%16 != 0)
            throw new IllegalStateException("The height("+height+") is below 16 or not a multiple of 16 or is greater 4064");
        this.height = height;
        return this;
    }

    public EnvironmentBuilder setInfiniburn(NamespacedKey infiniburn) {
        this.infiniburn = infiniburn;
        return this;
    }

    public EnvironmentBuilder setLogicalHeight(int logicalHeight) {
        this.logicalHeight = logicalHeight;
        return this;
    }

    public EnvironmentBuilder setMinY(int minY) {
        if (minY > 2016 || minY < -2032 || minY%16 != 0)
            throw new IllegalArgumentException("The minY("+minY+") is below -2032 or higher then 2016 or not a multiple of 16!");
        this.minY = minY;
        return this;
    }

    public EnvironmentBuilder setNatural(boolean natural) {
        this.natural = natural;
        return this;
    }

    public EnvironmentBuilder setPiglinSafe(boolean piglinSafe) {
        this.piglinSafe = piglinSafe;
        return this;
    }

    public EnvironmentBuilder setRespawnAnchorWorks(boolean respawnAnchorWorks) {
        this.respawnAnchorWorks = respawnAnchorWorks;
        return this;
    }

    public EnvironmentBuilder setUltraWarm(boolean ultraWarm) {
        this.ultraWarm = ultraWarm;
        return this;
    }

}
