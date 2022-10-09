package com.gabrielhd.worldgen.biome.methods;

import lombok.Getter;
import org.bukkit.Particle;

@Getter
public class Particles {

    private final float quantity;
    private final Particle particle;

    public Particles(Particle particle, float quantity) {
        this.particle = particle;
        this.quantity = quantity;
    }
}
