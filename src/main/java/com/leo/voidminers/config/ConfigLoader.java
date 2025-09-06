package com.leo.voidminers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.leo.voidminers.util.MapUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class ConfigLoader {
    public static final String CONFIG_FILE = "void-miners.json5";
    private static ConfigLoader INSTANCE = new ConfigLoader();

    private ConfigLoader() {}

    public static ConfigLoader getInstance() {
        return INSTANCE != null ? INSTANCE : new ConfigLoader();
    }

    @Expose
    public boolean ALLOW_NO_ENERGY_MINERS = false;

    // Global solar settings
    @Expose
    public boolean SOLAR_ALLOW_MOONLIGHT = true;

    // Maximum nighttime fraction of daytime output (0.0 = no night output)
    @Expose
    public float SOLAR_MOONLIGHT_MAX = 0.15f;

    @Expose
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new MinerConfig(1000000, 1000, 300,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("aurantium", new MinerConfig(2000000, 900, 350,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("citrinetine", new MinerConfig(3000000,800, 400,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("verdium", new MinerConfig(4000000,700, 450,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("azurine", new MinerConfig(5000000,600, 500,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("caerium", new MinerConfig(6000000,500, 550,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("amethystine", new MinerConfig(7000000,400, 600,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("rosarium", new MinerConfig(8000000,300, 650,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        ))
    );

    @Expose
    public Map<String, SolarConfig> SOLAR_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine",    new SolarConfig(1_000_000, 300)),
        MapUtil.createEntry("aurantium",   new SolarConfig(2_000_000, 350)),
        MapUtil.createEntry("citrinetine", new SolarConfig(3_000_000, 400)),
        MapUtil.createEntry("verdium",     new SolarConfig(4_000_000, 450)),
        MapUtil.createEntry("azurine",     new SolarConfig(5_000_000, 500)),
        MapUtil.createEntry("caerium",     new SolarConfig(6_000_000, 550)),
        MapUtil.createEntry("amethystine", new SolarConfig(7_000_000, 600)),
        MapUtil.createEntry("rosarium",    new SolarConfig(1_000_000_000, 54_087_893))
    );

    public void load() {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
                System.out.println("Configuration file does not exist. Creating a new one.");
                saveDefaultConfig(file, gson);
            } else {
                try (JsonReader jsonReader = new JsonReader(new FileReader(file))) {
                    INSTANCE = gson.fromJson(jsonReader, ConfigLoader.class);
                    if (INSTANCE == null) {
                        throw new JsonSyntaxException("Parsed configuration is null.");
                    }
                    // Backward compatibility: if SOLAR_CONFIGS missing in existing json,
                    // derive sensible defaults from MINER_CONFIGS
                    if (INSTANCE.SOLAR_CONFIGS == null || INSTANCE.SOLAR_CONFIGS.isEmpty()) {
                        INSTANCE.SOLAR_CONFIGS = deriveSolarFromMiner(INSTANCE.MINER_CONFIGS);
                    }
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println("Invalid configuration file. Regenerating default config.");
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new ConfigLoader();

            gson.toJson(INSTANCE, ConfigLoader.class, writer);
            System.out.println("Default configuration file created successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default configuration file.", e);
        }
    }

    public MinerConfig getMinerConfig(String name) {
        return MINER_CONFIGS.getOrDefault(name, new MinerConfig(0,0, 0, Map.of()));
    }

    public SolarConfig getSolarConfig(String name) {
        SolarConfig fallback = new SolarConfig(0, 0);
        if (SOLAR_CONFIGS != null && SOLAR_CONFIGS.containsKey(name))
            return SOLAR_CONFIGS.get(name);

        // Fallback to miner values if solar entry missing
        MinerConfig miner = MINER_CONFIGS.get(name);
        if (miner != null) return new SolarConfig(miner.energyStorage, miner.energyTick);
        return fallback;
    }

    public ModifierConfig getModifierConfig(String name, String type) {
        return getMinerConfig(name).modifiers.getOrDefault(type, new ModifierConfig(1, 1, 1));
    }

    public ModifierConfig getModifierConfig(Block block) {
        String blockName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        String minerTier = blockName.split("_")[0];
        String modifierType = blockName.split("_")[1];

        return getModifierConfig(minerTier, modifierType);
    }

    public record MinerConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyTick, @Expose Map<String, ModifierConfig> modifiers) {

        public static MinerConfig fromBuf(FriendlyByteBuf buf) {
            int energyStorage = buf.readInt();
            int duration = buf.readInt();
            int energy = buf.readInt();

            int entries = buf.readInt();

            Map<String, ModifierConfig> modifiers = new HashMap<>();

            for (int i = 0; i < entries; i++) {
                modifiers.put(
                    buf.readUtf(),
                    ModifierConfig.fromBuf(buf)
                );
            }

            return new MinerConfig(energyStorage, duration, energy, modifiers);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeInt(energyStorage);
            buf.writeInt(duration);
            buf.writeInt(energyTick);

            buf.writeInt(modifiers.size());

            modifiers.forEach((key, value) -> {
                buf.writeUtf(key);
                value.toBuf(buf);
            });
        }
    }

    public record ModifierConfig(@Expose float energy, @Expose float speed, @Expose float item) {
        public static ModifierConfig fromBuf(FriendlyByteBuf buf) {
            float energy = buf.readFloat();
            float speed = buf.readFloat();
            float item = buf.readFloat();

            return new ModifierConfig(energy, speed, item);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeFloat(energy);
            buf.writeFloat(speed);
            buf.writeFloat(item);
        }
    }

    public record SolarConfig(@Expose int energyStorage, @Expose int energyTick) {
        public static SolarConfig fromBuf(FriendlyByteBuf buf) {
            int energyStorage = buf.readInt();
            int energyTick = buf.readInt();
            return new SolarConfig(energyStorage, energyTick);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeInt(energyStorage);
            buf.writeInt(energyTick);
        }
    }

    private static Map<String, SolarConfig> deriveSolarFromMiner(Map<String, MinerConfig> minerConfigs) {
        Map<String, SolarConfig> map = new HashMap<>();
        minerConfigs.forEach((k, v) -> map.put(k, new SolarConfig(v.energyStorage, v.energyTick)));
        return map;
    }
}
