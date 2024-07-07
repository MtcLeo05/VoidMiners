package com.leo.voidminers.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.leo.voidminers.block.entity.ModifierBE;
import com.leo.voidminers.item.CrystalSet;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {

    public static CommentedConfig configData;
    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        for (CrystalSet set : CrystalSet.getAllSets()) {
            builder.push(set.name);

            builder.defineInRange("duration", 2500, 0, Integer.MAX_VALUE);
            builder.defineInRange("energy", 300, 0, Integer.MAX_VALUE);
            builder.defineList("energyMod", List.of(0.9f, 1f, 1f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0));
            builder.defineList("speedMod", List.of(1.1f, 0.95f, 1f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0));
            builder.defineList("itemMod", List.of(1.2f, 1f, 1.75f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0));

            builder.pop();
        }
    }

    public static int getDuration(@NotNull String name) {
        return SPEC.isLoaded() && configData != null ? configData.getIntOrElse(name + ".duration", 2500) : 2500;
    }

    public static int getEnergy(@NotNull String name) {
        return SPEC.isLoaded() && configData != null ? configData.getIntOrElse(name + ".energy", 350) : 350;
    }

    public static Float[] getModifiersFromTypeAndName(String name, ModifierBE.ModifierType type) {
        if (SPEC.isLoaded() && !type.equals(ModifierBE.ModifierType.NULL)) {
            List<?> list = configData.get(name + "." + type.type + "Mod");

            List<Float> values = new ArrayList<>();

            for (Object o : list) {
                if (o instanceof Double d) {
                    values.add(
                        d.floatValue()
                    );
                } else if (o instanceof Integer i) {
                    values.add(
                        i.floatValue()
                    );
                }
            }

            if(!values.isEmpty()){
                return values.toArray(new Float[0]);
            }
        }

        return new Float[]{1f, 1f, 1f};

    }
}
