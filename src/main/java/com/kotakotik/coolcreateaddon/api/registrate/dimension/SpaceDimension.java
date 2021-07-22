package com.kotakotik.coolcreateaddon.api.registrate.dimension;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.kotakotik.coolcreateaddon.CreateAddon;
import com.kotakotik.coolcreateaddon.api.SpaceRegistrate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class SpaceDimension {
    public static class Provider implements IDataProvider {
        @Override
        public void run(DirectoryCache dir) {
            Path path = generator.getOutputFolder();
            Set<ResourceLocation> set = Sets.newHashSet();
            forAll((dim) -> {
                if (!set.add(dim.getId())) {
                    throw new IllegalStateException("Duplicate recipe " + dim.getId());
                } else {
                    save(dir, dim.toJson(), path.resolve("data/" + dim.getId().getNamespace() + "/dimension/" + dim.getId().getPath() + ".json"));
                }
            });
        }

        @Override
        public String getName() {
            return "Space dimensions";
        }

        protected List<SpaceDimension> all = new ArrayList<>();
        protected DataGenerator generator;

        protected void forAll(Consumer<SpaceDimension> cons) {
            all.forEach(cons);
        }

        public Provider(DataGenerator generator) {
            this.generator = generator;
        }

        private static void save(DirectoryCache dir, JsonObject json, Path path) {
            try {
                String s = gson().toJson(json);
                String s1 = SHA1.hashUnencodedChars(s).toString();
                if (!Objects.equals(dir.getHash(path), s1) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(s);
                    }
                }

                dir.putNew(path, s1);
            } catch (IOException ioexception) {
                CreateAddon.LOGGER.error("Couldn't save dimension {}", path, ioexception);
            }

        }
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", id.toString());
        obj.add("generator", generator.toJson());
        return obj;
    }

    public ResourceLocation getId() {
        return null;
    }

    public final Generator generator;
    protected ResourceLocation id;

    public SpaceDimension(ResourceLocation id, Generator generator) {
        this.id = id;
        this.generator = generator;
    }

    protected static Gson gson() {return new GsonBuilder().setPrettyPrinting().create();}

    public static class Generator {
        public final String type;
        public final Settings settings;
        public final BiomeSource biomeSource;
        public final int seed;

        enum Type {
            FLAT,
            NOISE,
            DEBUG;

            public String getId() {
                return new ResourceLocation(name().toLowerCase()).toString();
            }
        }

        public Generator(String type, Settings settings, BiomeSource biomeSource, int seed) {
            this.type = type;
            this.settings = settings;
            this.biomeSource = biomeSource;
            this.seed = seed;
        }

        public Generator(Type type, Settings settings, BiomeSource biomeSource, int seed) {
            this(type.getId(), settings, biomeSource, seed);
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", type);
            obj.add("settings", settings.toJson());
            obj.add("biome_source", biomeSource.toJson());
            obj.addProperty("seed", seed);
            return obj;
        }

        public static class BiomeSource {
            public final List<ResourceLocation> biomes;
            public final ResourceLocation type;
            public final String seed;
            public final boolean largeBiomes;

            public BiomeSource(List<ResourceLocation> biomes, ResourceLocation type, String seed, boolean largeBiomes) {
                this.biomes = biomes;
                this.type = type;
                this.seed = seed;
                this.largeBiomes = largeBiomes;
            }

            public JsonObject toJson() {
                JsonObject obj = new JsonObject();
                if(biomes != null) {
                    obj.add("biomes", gson().toJsonTree(biomes.stream().map(ResourceLocation::toString)));
                }
                obj.addProperty("type", type.toString());
                obj.addProperty("seed", seed);
                obj.addProperty("large_biomes", largeBiomes);
                return obj;
            }

            public static class VanillaTypes {

            }
        }

        public static class Settings {
            public final Default defaultBlock;
            public final Default defaultFluid;
            public final int bedrockFloorPos;
            public final int bedrockRoofPos;
            public final int seaLevel;
            public final boolean disableMobSpawning;
            public final Noise noise;
            public final List<Pair<String, Structure>> structures;
            public final Stronghold stronghold;

            public static class Default extends Pair<ResourceLocation, HashMap<String, String>> {
                protected Default(ResourceLocation first, HashMap<String, String> second) {
                    super(first, second);
                }

                public JsonObject toJson() {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("Name", getFirst().toString());
                    JsonObject properties = new JsonObject();
                    getSecond().forEach(properties::addProperty);
                    obj.add("Properties", properties);
                    return obj;
                }
            }

            public Settings(Default defaultBlock, Default defaultFluid, int bedrockFloorPos, int bedrockRoofPos, int seaLevel, boolean disableMobSpawning, Noise noise, List<Pair<String, Structure>> structures, Stronghold stronghold) {
                this.defaultBlock = defaultBlock;
                this.defaultFluid = defaultFluid;
                this.bedrockFloorPos = bedrockFloorPos;
                this.bedrockRoofPos = bedrockRoofPos;
                this.seaLevel = seaLevel;
                this.disableMobSpawning = disableMobSpawning;
                this.noise = noise;
                this.structures = structures;
                this.stronghold = stronghold;
            }

            public JsonObject toJson() {
                JsonObject obj = new JsonObject();
                obj.add("default_block", defaultBlock.toJson());
                obj.add("default_fluid", defaultFluid.toJson());
                obj.addProperty("bedrock_floor_position", bedrockFloorPos);
                obj.addProperty("bedrock_roof_position", bedrockRoofPos);
                obj.addProperty("sea_level", seaLevel);
                obj.addProperty("disable_mob_generation", disableMobSpawning);
                obj.add("noise", noise.toJson());
                JsonObject structuresJ = new JsonObject();
                structuresJ.add("structures", gson().toJsonTree(structures.stream().map(s -> Pair.of(s.getFirst(), s.getSecond().toJson()))));
                structuresJ.add("stronghold", stronghold.toJson());
                obj.add("structures", structuresJ);
                return obj;
            }

            public static class Noise {
                public final Slide topSlide;
                public final Slide bottomSlide;
                public final Sampling sampling;
                public final int height;
                public final int sizeHorizontal;
                public final int sizeVertical;
                public final float densityOffset;
                public final boolean simplexSurfaceNoise;
                public final boolean randomDensityOffset;
                public final boolean islandNoiseOverride;
                public final boolean amplified;

                public static class Slide {
                    public final int target;
                    public final int size;
                    public final int offset;

                    public Slide(int target, int size, int offset) {
                        this.target = target;
                        this.size = size;
                        this.offset = offset;
                    }

                    public JsonObject toJson() {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("target", target);
                        obj.addProperty("size", size);
                        obj.addProperty("offset", offset);
                        return obj;
                    }
                }

                public static class Sampling {
                    public final int xzScale;
                    public final int yScale;
                    public final int xzFactor;
                    public final int yFactor;

                    public Sampling(int xzScale, int yScale, int xzFactor, int yFactor) {
                        this.xzScale = xzScale;
                        this.yScale = yScale;
                        this.xzFactor = xzFactor;
                        this.yFactor = yFactor;
                    }

                    public JsonObject toJson() {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("xz_scale", xzScale);
                        obj.addProperty("y_scale", yScale);
                        obj.addProperty("xz_factor", xzFactor);
                        obj.addProperty("y_factor", yFactor);
                        return obj;
                    }
                }

                public Noise(Slide topSlide, Slide bottomSlide, Sampling sampling, int height, int sizeHorizontal, int sizeVertical, float densityOffset, boolean simplexSurfaceNoise, boolean randomDensityOffset, boolean islandNoiseOverride, boolean amplified) {

                    this.topSlide = topSlide;
                    this.bottomSlide = bottomSlide;
                    this.sampling = sampling;
                    this.height = height;
                    this.sizeHorizontal = sizeHorizontal;
                    this.sizeVertical = sizeVertical;
                    this.densityOffset = densityOffset;
                    this.simplexSurfaceNoise = simplexSurfaceNoise;
                    this.randomDensityOffset = randomDensityOffset;
                    this.islandNoiseOverride = islandNoiseOverride;
                    this.amplified = amplified;
                }

                public JsonElement toJson() {
                    JsonObject obj = new JsonObject();
                    obj.add("top_slide", topSlide.toJson());
                    obj.add("bottom_slide", bottomSlide.toJson());
                    obj.add("sampling", sampling.toJson());
                    obj.addProperty("height", height);
                    obj.addProperty("sizeHorizontal", sizeHorizontal);
                    obj.addProperty("size_vertical", sizeVertical);
                    obj.addProperty("density_offset", densityOffset);
                    obj.addProperty("simplex_surface_noise", simplexSurfaceNoise);
                    obj.addProperty("random_density_offset", randomDensityOffset);
                    obj.addProperty("island_noise_override", islandNoiseOverride);
                    obj.addProperty("amplified", amplified);
                    return obj;
                }
            }

            public static class Stronghold {
                public final int distance;
                public final int spread;
                public final int count;

                public Stronghold(int distance, int spread, int count) {
                    this.distance = distance;
                    this.spread = spread;
                    this.count = count;
                }


                public JsonObject toJson() {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("distance", distance);
                    obj.addProperty("spread", spread);
                    obj.addProperty("count", count);
                    return obj;
                }
            }

            public static class Structure {
                public final int spacing;
                public final int separation;
                public final int salt;

                public Structure(int spacing, int separation, int salt) {
                    this.spacing = spacing;
                    this.separation = separation;
                    this.salt = salt;
                }

                public JsonObject toJson() {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("spacing", spacing);
                    obj.addProperty("separation", separation);
                    obj.addProperty("salt", salt);
                    return obj;
                }
            }
        }
    }
}
