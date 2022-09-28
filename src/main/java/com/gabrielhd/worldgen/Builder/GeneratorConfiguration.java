package com.gabrielhd.worldgen.Builder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.StructureType;

import java.util.HashMap;

@Getter @Setter
public class GeneratorConfiguration {

    private StructureGeneration structureGeneration = new StructureGeneration();
    private NoiseGeneration noiseGeneration = new NoiseGeneration();
    private Material defaultBlock = Material.STONE;
    private Material defaultFluid = Material.WATER;

    private int seaLevel = 63;
    private boolean disableMobGeneration = false;
    private boolean aquifersEnabled = true;
    private boolean noiseCavesEnabled = true;
    private boolean oreVeinsEnabled = true;
    private boolean noodleCavesEnabled = true;
    private boolean legacyRandomSource;
    private boolean canGenerateStructures;
    private boolean canGenerateDecoration;

    public GeneratorConfiguration() {
    }

    public void setDefaultBlock(Material defaultBlock) {
        if (defaultBlock == null || (!defaultBlock.isBlock() && !(defaultBlock != Material.WATER || defaultBlock != Material.LAVA)))
            throw new IllegalArgumentException("The default Block '"+defaultBlock+"' is not a Block or null!");

        this.defaultBlock = defaultBlock;
    }

    public void setDefaultFluid(Material defaultFluid) {
        if (defaultBlock == null || (defaultBlock != Material.WATER && defaultBlock != Material.LAVA && !defaultBlock.isBlock()))
            throw new IllegalArgumentException("The default Fluid '"+defaultBlock+"' is not a Fluid or null!");
        this.defaultFluid = defaultFluid;
    }

    public void setNoiseGeneration(NoiseGeneration noiseGeneration) {
        if (noiseGeneration == null)
            throw new IllegalArgumentException("NoiseGeneration can not be null!");
        this.noiseGeneration = noiseGeneration;
    }

    public void setStructureGeneration(StructureGeneration structureGeneration) {
        if (structureGeneration == null)
            throw new IllegalArgumentException("StructureGeneration can not be null!");
        this.structureGeneration = structureGeneration;
    }

    public enum RandomGenerationType {
        LEGACY,XOROSHIRO
    }

    @Getter
    public static class StructureInfo {
        private int spacing;
        private int separation;
        @Setter private int salt;

        public StructureInfo(int spacing, int separation, int salt) {
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }

        public void setSeparation(int separation) {
            if (separation >= spacing || separation < 1)
                throw new IllegalArgumentException("Seperation must be less then Spacing and greater 1!");
            this.separation = separation;
        }

        public void setSpacing(int spacing) {
            if (spacing <= separation || separation < 2)
                throw new IllegalArgumentException("Spacing must be greater seperation and greater 2");
            this.spacing = spacing;
        }
    }

    public static class StructureGeneration {
        private static final HashMap<StructureType, StructureInfo> structsetts = new HashMap<>();
        static {
            structsetts.put(StructureType.VILLAGE, new StructureInfo(32,8,10387312));
            structsetts.put(StructureType.DESERT_PYRAMID, new StructureInfo(32,8,14357617));
            structsetts.put(StructureType.IGLOO, new StructureInfo(32,8,14357618));
            structsetts.put(StructureType.JUNGLE_PYRAMID, new StructureInfo(32,8,14357619));
            structsetts.put(StructureType.SWAMP_HUT, new StructureInfo(32,8,14357620));
            structsetts.put(StructureType.PILLAGER_OUTPOST, new StructureInfo(32,8,165745296));
            structsetts.put(StructureType.STRONGHOLD, new StructureInfo(1,0,0));
            structsetts.put(StructureType.OCEAN_MONUMENT, new StructureInfo(32,5,10387313));
            structsetts.put(StructureType.END_CITY, new StructureInfo(20,11,10387313));
            structsetts.put(StructureType.WOODLAND_MANSION, new StructureInfo(80,20,10387319));
            structsetts.put(StructureType.BURIED_TREASURE, new StructureInfo(1,0,0));
            structsetts.put(StructureType.MINESHAFT, new StructureInfo(1,0,0));
            structsetts.put(StructureType.RUINED_PORTAL,new StructureInfo(25,10, 34222645));
            structsetts.put(StructureType.SHIPWRECK, new StructureInfo(24,4,165745295));
            structsetts.put(StructureType.OCEAN_RUIN, new StructureInfo(20,8,14357621));
            structsetts.put(StructureType.BASTION_REMNANT, new StructureInfo(27,4,30084232));
            structsetts.put(StructureType.NETHER_FORTRESS, new StructureInfo(27,4,30084232));
            structsetts.put(StructureType.NETHER_FOSSIL, new StructureInfo(2,1,14357921));
        }


        private HashMap<StructureType, StructureInfo> structureInfos = new HashMap<>();


        public StructureGeneration(boolean useOverworld) {
            if(useOverworld){
                structureInfos = (HashMap<StructureType, StructureInfo>) structsetts.clone();
            }
        }

        public StructureGeneration() {
            this(false);
        }

        public void addStructureTypeInfo(StructureType structureType,StructureInfo structureInfo) {
            if (structureType == null)
                throw new IllegalArgumentException("StructureType can not be null!");
            if (structureInfo == null)
                throw  new IllegalArgumentException("StructureInfo can not be null!");

            structureInfos.put(structureType,structureInfo);
        }

        public HashMap<StructureType,StructureInfo> getStructureInfos() {
            return structureInfos;
        }
    }

    @Getter @Setter
    public static class NoiseGeneration{
        private int minY = -64;
        private int height = 384;
        private SamplingGeneration samplingGeneration = new SamplingGeneration();
        private SliderGeneration topSlideSettings = new SliderGeneration(-0.078125D, 2, 8);
        private SliderGeneration bottomSlideSettings = new SliderGeneration(0.1171875D, 3, 0);
        private int noiseSizeHorizontal = 1;
        private int noiseSizeVertical = 2;
        private boolean islandNoiseOverride = false;
        private boolean isAmplified = false;
        private boolean largeBiomes = false;

        public NoiseGeneration() {

        }

        public void setBottomSlideSettings(SliderGeneration bottomSlideSettings) {
            if (bottomSlideSettings == null)
                throw new IllegalArgumentException("SliderGeneration can not be null!");
            this.bottomSlideSettings = bottomSlideSettings;
        }

        public void setSamplingGeneration(SamplingGeneration samplingGeneration) {
            if (samplingGeneration == null)
                throw new IllegalArgumentException("SamplingGeneration can not be null!");
            this.samplingGeneration = samplingGeneration;
        }

        public void setTopSlideSettings(SliderGeneration topSlideSettings) {
            if (topSlideSettings == null)
                throw new IllegalArgumentException("SliderGeneration can not be null!");
            this.topSlideSettings = topSlideSettings;
        }
    }

    @Getter @Setter
    public static class SliderGeneration {
        private double target = -0.078125D;
        private int size = 2;
        private int offset = 8;

        public SliderGeneration(double target, int size, int offset) {
            this.target = target;
            this.size = size;
            this.offset = offset;
        }

        public SliderGeneration() {}
    }

    @Getter @Setter
    public static class SamplingGeneration {

        private double xzScale = 1.0D;
        private double yScale = 1.0D;
        private double xzFactor = 80.0D;
        private double yFactor = 160.0D;

        public SamplingGeneration(double xzScale, double yScale, double xzFactor, double yFactor) {
            this.xzScale = xzScale;
            this.yScale = yScale;
            this.xzFactor = xzFactor;
            this.yFactor = yFactor;
        }

        public SamplingGeneration() {

        }
    }
}

