package com.gabrielhd.worldgen.biome;

import com.gabrielhd.worldgen.biome.methods.BiomeEntity;
import com.gabrielhd.worldgen.biome.methods.CaveSound;
import com.gabrielhd.worldgen.biome.methods.Music;
import com.gabrielhd.worldgen.biome.methods.Particles;
import com.gabrielhd.worldgen.biome.types.*;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_19_R1.CraftSound;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.logging.Level;


@Getter @Setter
public class BiomeCreator implements Keyed {

    private CaveSound caveSound = null;
    private BiomeCaveSoundSettings caveSoundSettings = null;
    private Sound ambientSound = null;
    private Particles particles = null;

    private final NamespacedKey key;

    private final HashMap<DecorationType, List<Decoration>> biomeFeatures = new HashMap<>();
    private final HashMap<DecorationType, List<CustomBiomeFeature>> biomeFeaturesNMS = new HashMap<>();

    private final HashMap<FeatureType, List<Carvers>> biomeCarvers = new HashMap<>();

    private final List<Structure> allowed_Structures = new ArrayList<>();
    private final List<BiomeEntity> biomeEntities = new ArrayList<>();

    private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;

    private BiomeGeography geography = BiomeGeography.NONE;
    private Precipitation precipitation = Precipitation.NONE;
    private TemperatureModifier temperaturmodifier = TemperatureModifier.NONE;

    private Music music = null;

    private Color grass = null;
    private Color foliage = null;
    private Color sky = new Color(0, 0, 0);
    private Color waterfog = new Color(0,0,0);
    private Color fog = new Color(0,0,0);
    private Color water = new Color(0,0,0);

    //private float depth = 0.125F;
    //private float scale = 0.05F;
    private float temper = 0.8F;
    private float downfall = 0.4F;
    private float mobprobability = 0.1F;


    public BiomeCreator(Plugin plugin, String biomename){
        this.key = new NamespacedKey(plugin, biomename);
    }

    public BiomeCreator(String namespace, String biomename){
        this.key = new NamespacedKey(namespace, biomename);
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public List<Structure> getAllowedBiomeStructures(){
        return ImmutableList.copyOf(allowed_Structures);
    }

    public void setAllowedBiomeStructure(Structure structure, boolean allowed){
        if(allowed_Structures.contains(structure) && !allowed){
            allowed_Structures.remove(structure);
        }
        if(!allowed_Structures.contains(structure) && allowed){
            allowed_Structures.add(structure);
        }
    }

    public boolean isBiomeStructureAllowed(Structure structure){
        return allowed_Structures.contains(structure);
    }

    public void setTemperature(float val) {
        if(val < 1.0 && val >= 0.0){
            this.temper = val;
        }else{
            throw new IllegalArgumentException("Value is out of Range (0.0 >= val < 1.0)");
        }
    }

    public void setDownfall(float val) {
        if(val < 1.0 && val >= 0.0){
            this.downfall = val;
        }else{
            throw new IllegalArgumentException("Value is out of Range (0.0 >= val < 1.0)");
        }
    }

    public boolean addEntityConfiguration(EntityType type, int weight, int mincount, int maxcount){
        return biomeEntities.add(new BiomeEntity(type,weight,mincount,maxcount));
    }

    public boolean addEntityConfiguration(BiomeEntity biomeEntity){
        return biomeEntities.add(biomeEntity);
    }

    /**
     * Sets the MobSpawn-Probability of the CustomBiome
     * Larger values result in more Spawn-Attemps for Entitys
     *
     * @param val Value between 0.0 and 1.0
     * */
    public void setMobSpawnProbability(float val) {
        if(val < 1.0 && val >= 0.0){
            this.mobprobability = val;
        }else{
            throw new IllegalArgumentException("Value is out of Range (0.0 >= val < 1.0)");
        }
    }

    public boolean addBiomeFeature(DecorationType decorationType, CustomBiomeFeature biomeFeature){
        if(!this.biomeFeaturesNMS.containsKey(decorationType)) {
            this.biomeFeaturesNMS.put(decorationType,new ArrayList<>());
        }
        return biomeFeaturesNMS.get(decorationType).add(biomeFeature);
    }

    public boolean addBiomeFeature(DecorationType decorationType, Decoration biomeFeature){
        if(!this.biomeFeatures.containsKey(decorationType)) {
            this.biomeFeatures.put(decorationType,new ArrayList<>());
        }
        return biomeFeatures.get(decorationType).add(biomeFeature);
    }

    public List<Decoration> getBiomeFeatures(DecorationType decorationType){
        if(this.biomeFeatures.containsKey(decorationType)) {
            return this.biomeFeatures.get(decorationType);
        }
        return null;
    }

    public boolean addBiomeCarver(FeatureType featureType, Carvers biomeCarver){
        if(!biomeCarvers.containsKey(featureType)){
            biomeCarvers.put(featureType,new ArrayList<>());
        }
        return biomeCarvers.get(featureType).add(biomeCarver);
    }

    public List<Carvers> getBiomeCarvers(FeatureType featureType){
        if(biomeCarvers.containsKey(featureType)){
            return biomeCarvers.get(featureType);
        }
        return null;
    }

    public void setCustomMusic(Sound sound, int min_delay, int max_delay, boolean replace_current_music){
        this.setCustomMusic(new Music(sound,min_delay,max_delay,replace_current_music));
    }

    public void setCustomMusic(Music music){
        this.music = music;
    }

    private static int colorToHexaDecimal(Color col){
        String hex = String.format("%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue());
        return Integer.parseInt(hex, 16);
    }

    private static net.minecraft.world.entity.EntityType convertBukkitEntityTypeToNMS(EntityType entitytype) {
        try {
            return net.minecraft.world.entity.EntityType.byString(entitytype.getKey().getKey()).orElse(null);

        }catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public CustomBiome createBiome(){
        return createBiome(false);
    }

    public CustomBiome createBiome(boolean replace){
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        RegistryAccess registryAccess = craftServer.getServer().registryAccess();
        WritableRegistry<Biome> biomeRegistry = registryAccess.ownedRegistryOrThrow(Registry.BIOME_REGISTRY);

        String namespace = getKey().getNamespace();
        String key = getKey().getKey();
        ResourceKey<Biome> resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(namespace,key));

        Optional<Biome> optionalBiome = BuiltinRegistries.BIOME.getOptional(resourceKey);

        if(optionalBiome.isPresent() && !replace){
            Biome endbiome = optionalBiome.get();
            CustomBiome customBiome = new CustomBiome(endbiome, BuiltinRegistries.BIOME.getResourceKey(endbiome).get());
            return customBiome;
        }

        Biome createdBiomeBase = createBiomeBase();

        if(!optionalBiome.isPresent()) {
            //BuiltinRegistries.BIOME.register(resourceKey,createdBiomeBase,Lifecycle.stable());
            biomeRegistry.register(resourceKey,createdBiomeBase,Lifecycle.stable());
            Biome biomedata = BuiltinRegistries.registerMapping(BuiltinRegistries.BIOME, resourceKey, createdBiomeBase);
            //optionalBiome = biomeRegistry.getOptional(resourceKey);
            optionalBiome = BuiltinRegistries.BIOME.getOptional(resourceKey);
         //   System.out.print("was not there: now ? " + optionalBiome.isPresent());
        }else{
            Biome targetBiome = optionalBiome.get();

            boolean overwrite = overwriteFields(targetBiome,createdBiomeBase);

            if(overwrite) {
                AdvancedWorldCreatorAPI.main.getLogger().log(Level.INFO, "Custom-Biome was replaced successfully!");
            }

         //   AdvancedWorldCreatorAPI.main.getLogger().log(Level.INFO, "Custom-Biome was replaced successfully!");
            optionalBiome = BuiltinRegistries.BIOME.getOptional(resourceKey);
          //  System.out.println("Optional: " + optionalBiome.isPresent());
        }


        Biome endbiome = optionalBiome.get();

        return new CustomBiome(endbiome, BuiltinRegistries.BIOME.getResourceKey(endbiome).get());
    }



    private boolean overwriteFields(Biome target, Biome input){
        try {
            for (Field field : Biome.class.getDeclaredFields()) {
                String s = field.getName();
                if(s.equals("j") || s.equals("k") || s.equals("l") || s.equals("m") || s.equals("n")) {
                    field.setAccessible(true);

                    Object newvalue = field.get(input);
                    field.set(target, newvalue);
                }
            }

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @Getter
    public static class CustomBiome {
        private final ResourceKey<Biome> resourceKey;
        private final Biome biome;

        private CustomBiome(Biome biome, ResourceKey<Biome> resourceKey){
            Objects.requireNonNull(biome,"The BiomeBase can't be null!");
            Objects.requireNonNull(resourceKey,"The ResourcKey can't be null!");

            this.biome = biome;
            this.resourceKey = resourceKey;
        }
    }



    private Biome createBiomeBase() {
        BiomeGenerationSettings.Builder bisege = (new BiomeGenerationSettings.Builder());

        for(DecorationType decorationType : this.biomeFeatures.keySet()){
            for(Decoration biomeFeature : biomeFeatures.get(decorationType)){
                bisege.addFeature(decorationType.get(), biomeFeature.getFeature());
            }
        }
        for(DecorationType decorationType : getAllCustomBiomeFeatures().keySet()){
            for(CustomBiomeFeature biomeFeature : biomeFeaturesNMS.get(decorationType)){
                bisege.addFeature(decorationType.get(),biomeFeature.getFeature());
            }
        }

        MobSpawnSettings.Builder bisemo = new MobSpawnSettings.Builder();

        bisemo.creatureGenerationProbability(getMobSpawnProbability());

        for(BiomeEntity be : getBiomeEntities()){
            net.minecraft.world.entity.EntityType nmsentity = convertBukkitEntityTypeToNMS(be.getEntityType());

            if(nmsentity != null){
                MobCategory enumCreatureType = nmsentity.getCategory();

                bisemo.addSpawn(enumCreatureType, new MobSpawnSettings.SpawnerData(nmsentity, be.getWeight(), be.getMinCount(), be.getMaxCount()));

            }

        }



        BiomeSpecialEffects.Builder bf = new BiomeSpecialEffects.Builder();


        bf.fogColor(colorToHexaDecimal(getFogColor()));
        bf.waterColor(colorToHexaDecimal(getWaterColor()));
        bf.waterFogColor(colorToHexaDecimal(getWaterFogColor()));
        bf.skyColor(colorToHexaDecimal(getSkyColor()));

        if(getFoliageColor() != null) {
            bf.foliageColorOverride(colorToHexaDecimal(getFoliageColor()));
        }
        if(getGrassColor() != null) {
            bf.grassColorOverride(colorToHexaDecimal(getGrassColor()));
        }

        Music music = getCustomMusic();
        if(music != null) {
            bf.backgroundMusic(new net.minecraft.sounds.Music(CraftSound.getSoundEffect(music.getSound()),music.getMinDelay(),music.getMaxDelay(),music.isReplacingCurrentMusic()));
        }
        CaveSound caveSound = getCaveSound();
        if(caveSound != null) {
            bf.ambientAdditionsSound(new AmbientAdditionsSettings(CraftSound.getSoundEffect(caveSound.getSound()),caveSound.getTickChance()));
        }

        BiomeCaveSoundSettings caveSoundSettings = getCaveSoundSettings();
        if(caveSoundSettings != null){
            bf.ambientMoodSound(new AmbientMoodSettings(CraftSound.getSoundEffect(caveSoundSettings.getSound()),caveSoundSettings.getTickDelay(),caveSoundSettings.getBlockSearchExtent(),caveSoundSettings.getOffset()));
        }
        Sound ambientSound = getAmbientSound();
        if(ambientSound != null) {
            bf.ambientLoopSound(CraftSound.getSoundEffect(ambientSound));
        }
        Particles particles = getParticles();
        if(particles != null) {
            bf.ambientParticle(new AmbientParticleSettings(CraftParticle.toNMS(particles.getParticle()), particles.getQuantity()));
        }

        bf.grassColorModifier(getBiomeGrassColorModifier().getGrassColorModifier());

        Biome.BiomeBuilder bb = new Biome.BiomeBuilder();


        bb.biomeCategory(getGeography().getGeography());
        bb.precipitation(getPrecipitation().getPrecipitation());
        bb.temperatureAdjustment(getTemperatureModifier().getTemperaturModifier());
        bb.specialEffects(bf.build());
        bb.generationSettings(bisege.build());
        bb.mobSpawnSettings(bisemo.build());

        bb.temperature(getTemperature());
        bb.downfall(getDownfall());

        return bb.build();
    }
}
