package com.gabrielhd.worldgen;

import com.gabrielhd.worldgen.biome.CustomBiomeProvider;
import com.gabrielhd.worldgen.builder.EnvironmentBuilder;
import com.gabrielhd.worldgen.builder.GeneratorConfiguration;
import com.gabrielhd.worldgen.builder.NoiseRouterBuilder;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Main;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.generator.CraftWorldInfo;
import org.bukkit.craftbukkit.v1_19_R1.generator.CustomWorldChunkManager;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class CustomWorldGenAPI {

    public static World createWorld(CustomWorldCreator creator) {
        Server server = Bukkit.getServer();
        CraftServer craftServer = (CraftServer) server;

        Validate.notNull(creator, "Creator may not be null");
        String name = creator.name();
        ChunkGenerator generator = creator.generator();
        BiomeProvider biomeProvider = creator.biomeProvider();
        CustomBiomeProvider customBiomeProvider = creator.getCustomBiomeProvider();
        File folder = new File(craftServer.getWorldContainer(), name);
        World world = craftServer.getWorld(name);
        if (world != null) {
            return world;
        } else if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        } else {
            if (generator == null) {
                generator = craftServer.getGenerator(name);
            }

            if (biomeProvider == null) {
                biomeProvider = craftServer.getBiomeProvider(name);
            }

            ResourceKey<LevelStem> actualDimension;
            switch (creator.environment().ordinal()) {
                case 0 -> actualDimension = LevelStem.OVERWORLD;
                case 1 -> actualDimension = LevelStem.NETHER;
                case 2 -> actualDimension = LevelStem.END;
                case 3 -> {
                    if (creator.getEnvironmentBuilder() == null)
                        throw new IllegalArgumentException("Selected Environment.CUSTOM but not specified an EnvironmentBuilder!");
                    actualDimension = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation(name));
                }
                default -> throw new IllegalArgumentException("Illegal dimension");
            }

            LevelStorageSource.LevelStorageAccess worldSession;
            try {
                worldSession = LevelStorageSource.createDefault(craftServer.getWorldContainer().toPath()).createAccess(name, actualDimension);
            } catch (IOException var23) {
                throw new RuntimeException(var23);
            }

            PrimaryLevelData worlddata = null;
            if (worlddata == null) {
                DedicatedServerProperties.WorldGenProperties worldGenProperties = new DedicatedServerProperties.WorldGenProperties(String.valueOf(creator.seed()), GsonHelper.parse((creator.generatorSettings().isEmpty()) ? "{}" : creator.generatorSettings()), creator.generateStructures(), creator.type().getName());
                WorldGenSettings generatorsettings = worldGenProperties.create(craftServer.getServer().registryHolder);

                LevelSettings worldSettings = new LevelSettings(name, GameType.byId(craftServer.getDefaultGameMode().getValue()), creator.hardcore(), Difficulty.EASY, false, new GameRules(), craftServer.getServer().datapackconfiguration);

                worlddata = new PrimaryLevelData(worldSettings, generatorsettings, Lifecycle.stable());
            }

            worlddata.checkName(name);
            worlddata.setModdedInfo(craftServer.getServer().getServerModName(), craftServer.getServer().getModdedStatus().shouldReportAsModified());

            if (craftServer.getServer().options.has("forceUpgrade")) {
                Main.forceUpgrade(worldSession, DataFixers.getDataFixer(), craftServer.getServer().options.has("eraseCache"), () -> true, worlddata.worldGenSettings());
            }

            MappedRegistry<LevelStem> registrymaterials = (MappedRegistry<LevelStem>) worlddata.worldGenSettings().dimensions();
            GeneratorConfiguration generatorConfiguration = creator.getGeneratorConfiguration();
            ResourceKey<NoiseGeneratorSettings> generatorSettingBaseResourceKey = null;
            ResourceLocation worldMinecraftKey = new ResourceLocation("minecraft",creator.name());

            if(generatorConfiguration != null){
                generatorSettingBaseResourceKey = createGeneratorSettingBase(worldMinecraftKey,generatorConfiguration);
            }
            if (creator.environment() == World.Environment.CUSTOM) {
                createAndRegisterCustomEnvironment(actualDimension,registrymaterials,worldMinecraftKey,creator.getEnvironmentBuilder(),creator.seed(),generatorSettingBaseResourceKey);
            }

            RegistryAccess registryAccess = craftServer.getServer().registryAccess();
            WritableRegistry<NormalNoise.NoiseParameters> noiseRegistry = (WritableRegistry<NormalNoise.NoiseParameters>) registryAccess.ownedRegistryOrThrow(Registry.NOISE_REGISTRY);

            LevelStem worlddimension = registrymaterials.get(actualDimension);
            DimensionType dimensionmanager;
            net.minecraft.world.level.chunk.ChunkGenerator chunkgenerator;

            if (worlddimension == null) {
                LogManager.getLogger().error("WorldDimension null");

                return null;
            } else {
                dimensionmanager = worlddimension.typeHolder().value();
                chunkgenerator = worlddimension.generator();
            }

            WorldInfo worldInfo = new CraftWorldInfo(worlddata, worldSession, creator.environment(), dimensionmanager);
            if (biomeProvider == null && generator != null) {
                biomeProvider = generator.getDefaultBiomeProvider(worldInfo);
            }

            if (biomeProvider != null) {
                BiomeSource worldChunkManager = new CustomWorldChunkManager(worldInfo, biomeProvider, craftServer.getServer().registryHolder.ownedRegistryOrThrow(Registry.BIOME_REGISTRY));
                if (chunkgenerator instanceof NoiseBasedChunkGenerator) {
                    chunkgenerator = new NoiseBasedChunkGenerator(chunkgenerator.structureSets, ((NoiseBasedChunkGenerator)chunkgenerator).noises, worldChunkManager, ((NoiseBasedChunkGenerator)chunkgenerator).settings);
                }
            } else if(customBiomeProvider != null){
                BiomeSource worldChunkManager = new CustomChunkManager(worldInfo, customBiomeProvider, craftServer.getServer().registryHolder.ownedRegistryOrThrow(net.minecraft.core.Registry.BIOME_REGISTRY));
                if (chunkgenerator instanceof NoiseBasedChunkGenerator) {
                    chunkgenerator = new NoiseBasedChunkGenerator(chunkgenerator.structureSets, ((NoiseBasedChunkGenerator)chunkgenerator).noises, worldChunkManager, ((NoiseBasedChunkGenerator)chunkgenerator).settings);
                }
            }

            if(!worlddimension.generator().equals(chunkgenerator)) {
                try {
                    Field field = worlddimension.getClass().getDeclaredField("g");
                    field.setAccessible(true);

                    field.set(worlddimension, chunkgenerator);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            String levelName = craftServer.getServer().getProperties().levelName;
            ResourceKey worldKey;
            if (name.equals(levelName + "_nether")) {
                worldKey = net.minecraft.world.level.Level.NETHER;
            } else if (name.equals(levelName + "_the_end")) {
                worldKey = net.minecraft.world.level.Level.END;
            } else {
                worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(name.toLowerCase(Locale.ENGLISH)));
            }

            long obfuscateSeed = BiomeManager.obfuscateSeed(creator.seed());
            List<CustomSpawner> list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(worlddata));
            ServerLevel internal = new ServerLevel(craftServer.getServer(), craftServer.getServer().executor, worldSession, worlddata, worldKey,
                    worlddimension, craftServer.getServer().progressListenerFactory.create(11), worlddata.worldGenSettings().isDebug(), obfuscateSeed,
                    creator.environment() == World.Environment.NORMAL || creator.environment() == World.Environment.CUSTOM ? list : ImmutableList.of(),
                    true, creator.environment(), generator, biomeProvider);

            if (craftServer.getWorld(name.toLowerCase(Locale.ENGLISH)) == null) {
                return null;
            } else {
                craftServer.getServer().initWorld(internal, worlddata, worlddata, worlddata.worldGenSettings());
                internal.setSpawnSettings(true, true);
                craftServer.getServer().addLevel(internal);

                craftServer.getServer().prepareLevels(internal.getChunkSource().chunkMap.progressListener, internal);
                internal.entityManager.tick();

                craftServer.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));

                return internal.getWorld();
            }
        }
    }

    private static void createAndRegisterCustomEnvironment(ResourceKey<LevelStem> actualDimension, MappedRegistry<LevelStem> registrymaterials, ResourceLocation minecraftKey, EnvironmentBuilder builder, long seed, ResourceKey<NoiseGeneratorSettings> generatorSettingBaseResourceKey) {
        Server server = Bukkit.getServer();
        CraftServer craftServer = (CraftServer) server;

        ResourceKey<DimensionType> resourceKeyDimension = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, minecraftKey);
        WritableRegistry<DimensionType> registryDimensions = (WritableRegistry<DimensionType>) craftServer.getHandle().getServer().registryAccess().ownedRegistryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);

        turnOffFreeze(registrymaterials, (MappedRegistry<?>) registryDimensions);

        DimensionType dimensionManager = new DimensionType(builder.getFixedTime() == null ? OptionalLong.empty() : OptionalLong.of(builder.getFixedTime()),
                builder.isHasSkylight(),
                builder.isHasCeiling(),
                builder.isUltraWarm(),
                builder.isNatural(),
                builder.getCoordinateScale(),
                builder.isBedWorks(),
                builder.isRespawnAnchorWorks(),
                builder.getMinY(),
                builder.getHeight(),
                builder.getLogicalHeight(),
                TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(builder.getInfiniburn().getNamespace(), builder.getInfiniburn().getKey())),
                new ResourceLocation(builder.getEffectsLocation().getNamespace(), builder.getEffectsLocation().getKey()),
                builder.getAmbientLight(),
                new DimensionType.MonsterSettings(builder.isPiglinSafe(), builder.isHasRaids(), new IntProvider() {
                    @Override
                    public int sample(RandomSource randomSource) {
                        return 0;
                    }

                    @Override
                    public int getMinValue() {
                        return 0;
                    }

                    @Override
                    public int getMaxValue() {
                        return 15;
                    }

                    @Override
                    public IntProviderType<?> getType() {
                        return IntProviderType.CONSTANT;
                    }
                }, new Random().nextInt(15)));

        registryDimensions.registerOrOverride(OptionalInt.empty(), resourceKeyDimension, dimensionManager, Lifecycle.stable());

        NoiseBasedChunkGenerator generatorAbstract;
        if (generatorSettingBaseResourceKey == null) {
            generatorAbstract = makeDefaultOverworld(craftServer.getServer().registryHolder, true);
        } else {
            generatorAbstract = makeOverworld(craftServer.getServer().registryHolder, generatorSettingBaseResourceKey, true);
        }

        LevelStem dimension = new LevelStem(Holder.direct(dimensionManager), generatorAbstract);

        registrymaterials.registerOrOverride(OptionalInt.empty(), actualDimension, dimension, Lifecycle.stable());

        turnOnFreeze((MappedRegistry<?>) registryDimensions, registrymaterials);
    }

    private static ResourceKey<NoiseGeneratorSettings> createGeneratorSettingBase(ResourceLocation minecraftKey, GeneratorConfiguration generatorConfiguration) {
        Server server = Bukkit.getServer();
        CraftServer craftServer = (CraftServer) server;

        GeneratorConfiguration.NoiseGeneration noiseGeneration = generatorConfiguration.getNoiseGeneration();
        GeneratorConfiguration.StructureGeneration structureGeneration = generatorConfiguration.getStructureGeneration();
        HashMap<StructureType, GeneratorConfiguration.StructureInfo> strucctureInfos = structureGeneration.getStructureInfos();

        NoiseSettings noiseSettings = new NoiseSettings(noiseGeneration.getMinY(),noiseGeneration.getHeight(), noiseGeneration.getNoiseSizeHorizontal(),noiseGeneration.getNoiseSizeVertical());

        BlockState baseBlock = ((CraftBlockData)Bukkit.createBlockData(generatorConfiguration.getDefaultBlock())).getState();
        BlockState baseFluid = ((CraftBlockData)Bukkit.createBlockData(generatorConfiguration.getDefaultFluid())).getState();

        Registry<DensityFunction> iregistry = BuiltinRegistries.DENSITY_FUNCTION;
        NoiseRouter noiseRouter = NoiseRouterBuilder.overworld(iregistry);

        NoiseGeneratorSettings generatorSettingsBase = new NoiseGeneratorSettings(noiseSettings, baseBlock, baseFluid, noiseRouter, SurfaceRuleData.overworld(), (new OverworldBiomeBuilder()).spawnTarget(), generatorConfiguration.getSeaLevel(), generatorConfiguration.isDisableMobGeneration(), generatorConfiguration.isAquifersEnabled(), generatorConfiguration.isOreVeinsEnabled(), generatorConfiguration.isLegacyRandomSource());

        ResourceKey<NoiseGeneratorSettings> generatorSettingBaseResourceKey = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, minecraftKey);
        WritableRegistry<NoiseGeneratorSettings> registryGeneratorSettings = (WritableRegistry<NoiseGeneratorSettings>) craftServer.getHandle().getServer().registryAccess().ownedRegistryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);

        turnOffFreeze((MappedRegistry<?>) registryGeneratorSettings);
        registryGeneratorSettings.registerOrOverride(OptionalInt.empty(), generatorSettingBaseResourceKey, generatorSettingsBase, Lifecycle.stable());
        turnOnFreeze((MappedRegistry<?>) registryGeneratorSettings);
        return generatorSettingBaseResourceKey;
    }

    private static void turnOffFreeze(MappedRegistry<?>... registries) {
        for(MappedRegistry<?> registry : registries) {
            try {
                Field registryField = registry.getClass().getDeclaredField("ca");
                registryField.setAccessible(true);

                if((boolean) registryField.get(registry)) {
                    registryField.set(registry, false);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void turnOnFreeze(MappedRegistry<?>... registries) {
        for(MappedRegistry<?> registry : registries) {
            try {
                Field registryField = registry.getClass().getDeclaredField("ca");
                registryField.setAccessible(true);

                if(!(boolean) registryField.get(registry)) {
                    registryField.set(registry, true);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static NoiseBasedChunkGenerator makeDefaultOverworld(RegistryAccess iregistrycustom, boolean flag) {
        return makeOverworld(iregistrycustom, NoiseGeneratorSettings.OVERWORLD, flag);
    }

    public static NoiseBasedChunkGenerator makeOverworld(RegistryAccess iregistrycustom, ResourceKey<NoiseGeneratorSettings> resourcekey, boolean flag) {
        Registry<Biome> iregistry = iregistrycustom.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<StructureSet> iregistry1 = iregistrycustom.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
        Registry<NoiseGeneratorSettings> iregistry2 = iregistrycustom.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        Registry<NormalNoise.NoiseParameters> iregistry3 = iregistrycustom.registryOrThrow(Registry.NOISE_REGISTRY);

        return new NoiseBasedChunkGenerator(iregistry1, iregistry3, MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(iregistry, flag), iregistry2.getOrCreateHolder(resourcekey).result().orElseThrow());
    }
}
