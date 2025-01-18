package com.leo.voidminers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.leo.voidminers.util.MapUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLPaths;

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
    public boolean MINE_PREVIOUS_TIER = true;

    @Expose
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new MinerConfig(1000, 300,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("aurantium", new MinerConfig(900, 350,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("citrinetine", new MinerConfig(800, 400,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("verdium", new MinerConfig(700, 450,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("azurine", new MinerConfig(600, 500,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("caerium", new MinerConfig(500, 550,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("amethystine", new MinerConfig(400, 600,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("rosarium", new MinerConfig(300, 650,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        ))
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
        return MINER_CONFIGS.getOrDefault(name, new MinerConfig(0, 0, Map.of()));
    }

    public ModifierConfig getModifierConfig(String name, String type) {
        return getMinerConfig(name).modifiers.getOrDefault(type, new ModifierConfig(1, 1, 1));
    }

    public record MinerConfig(@Expose int duration, @Expose int energy, @Expose Map<String, ModifierConfig> modifiers) {

        public static MinerConfig fromBuf(FriendlyByteBuf buf) {
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

            return new MinerConfig(duration, energy, modifiers);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeInt(duration);
            buf.writeInt(energy);

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
}
