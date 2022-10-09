package com.gabrielhd.worldgen.biome.methods;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class Music {

    private final Sound sound;
    private final int mindelay;
    private final int maxdelay;
    private final boolean replace_current_music;

    public Music(Sound sound, int min_delay, int max_delay, boolean replace_current_music) {
        if(sound == null) throw new IllegalArgumentException("Sound can't be null!");
        if(min_delay < 0) throw new IllegalArgumentException("Min_Delay has to be greater 0!");
        if(min_delay < max_delay) throw new IllegalArgumentException("Max_Delay has to be greater or equal Min_Delay!");

        this.sound = sound;
        this.mindelay = min_delay;
        this.maxdelay = max_delay;
        this.replace_current_music = replace_current_music;
    }
}
