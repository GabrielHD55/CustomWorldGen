package com.gabrielhd.worldgen.biome.methods;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class CaveSound {

    private final Sound sound;
    private final double tickchance;

    public CaveSound(Sound sound, double tickchance) {
        if(sound == null) throw new IllegalArgumentException("Sound can't be null!");
        if(tickchance < 0 || tickchance > 1.0) throw new IllegalArgumentException("Tickchance is out of range (0.0 < tickchance <= 1.0)");

        this.sound = sound;
        this.tickchance = tickchance;
    }

    @Getter
    public static class SoundSettings {
        private final Sound sound;
        private final int tickDelay;
        private final int blockSearchExtent;
        private final double offset;

        public SoundSettings(Sound sound, int tick_delay, int block_search_extent, double offset){
            this.sound = sound;
            this.tickDelay = tick_delay;
            this.blockSearchExtent = block_search_extent;
            this.offset = offset;
        }
    }
}
