package nadiendev.voidminers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.util.MapUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;

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

    public void load() {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
                VoidMiners.LOGGER.info("Configuration file does not exist. Creating a new one.");
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
            VoidMiners.LOGGER.error("Invalid configuration file. Regenerating default config.");
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new ConfigLoader();

            gson.toJson(INSTANCE, ConfigLoader.class, writer);
            VoidMiners.LOGGER.info("Default configuration file created successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default configuration file.", e);
        }
    }

    public MinerConfig getMinerConfig(String name) {
        return MINER_CONFIGS.getOrDefault(name, new MinerConfig(0,0, 0, Map.of()));
    }

    public ModifierConfig getModifierConfig(String name, String type) {
        return getMinerConfig(name).modifiers.getOrDefault(type, new ModifierConfig(1, 1, 1));
    }

    public ModifierConfig getModifierConfig(Block block) {
        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        String minerTier = blockName.split("_")[0];
        String modifierType = blockName.split("_")[1];

        return getModifierConfig(minerTier, modifierType);
    }

    public record MinerConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyTick, @Expose Map<String, ModifierConfig> modifiers) {

        public static final StreamCodec<ByteBuf, MinerConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            MinerConfig::energyStorage,
            ByteBufCodecs.INT,
            MinerConfig::duration,
            ByteBufCodecs.INT,
            MinerConfig::energyTick,
            ByteBufCodecs.map(
                HashMap::new,
                ByteBufCodecs.STRING_UTF8,
                ModifierConfig.STREAM_CODEC
            ),
            MinerConfig::modifiers,
            MinerConfig::new
        );
    }

    public record ModifierConfig(@Expose float energy, @Expose float speed, @Expose float item) {
        public static final StreamCodec<ByteBuf, ModifierConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            ModifierConfig::energy,
            ByteBufCodecs.FLOAT,
            ModifierConfig::speed,
            ByteBufCodecs.FLOAT,
            ModifierConfig::item,
            ModifierConfig::new
        );
    }
}