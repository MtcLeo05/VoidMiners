package com.leo.voidminers.config;

import com.leo.voidminers.block.entity.ModifierBE;
import com.leo.voidminers.item.CrystalSet;
import com.leo.voidminers.util.MiscUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue PREVIOUS_TIER;
    public static final List<ForgeConfigSpec.IntValue> DURATION_CONFIG = new ArrayList<>();
    public static final List<ForgeConfigSpec.IntValue> ENERGY_CONFIG = new ArrayList<>();
    public static final List<ForgeConfigSpec.ConfigValue<List<? extends Float>>> ENERGY_MOD_CONFIG = new ArrayList<>();
    public static final List<ForgeConfigSpec.ConfigValue<List<? extends Float>>> SPEED_MOD_CONFIG = new ArrayList<>();
    public static final List<ForgeConfigSpec.ConfigValue<List<? extends Float>>> ITEM_MOD_CONFIG = new ArrayList<>();
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        PREVIOUS_TIER = builder
            .comment("Whether miners should mine resources from previous tiers, or only their own.")
            .define("previousMinerTier", true);

        List<CrystalSet> sets = CrystalSet.sets();

        for (int j = 0; j < sets.size(); j++) {
            CrystalSet set = sets.get(j);
            builder.push(set.name);

            DURATION_CONFIG.add(builder.defineInRange("duration", 1000 - (j * 200), 0, Integer.MAX_VALUE));
            ENERGY_CONFIG.add((builder.defineInRange("energy", 300 + (j * 50), 0, Integer.MAX_VALUE)));
            ENERGY_MOD_CONFIG.add(builder.defineList("energyMod", List.of(0.9f, 1f, 1f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0)));
            SPEED_MOD_CONFIG.add(builder.defineList("speedMod", List.of(1.1f, 0.95f, 1f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0)));
            ITEM_MOD_CONFIG.add(builder.defineList("itemMod", List.of(1.2f, 1f, 1.75f), (obj) -> (obj instanceof Double d && d > 0) || (obj instanceof Integer i && i > 0)));

            builder.pop();
        }


        SPEC = builder.build();
    }

    public static boolean shouldMinePreviousTiers(){
        return PREVIOUS_TIER.get();
    }

    public static int getDuration(@NotNull String name) {
        int i = MiscUtil.tierMap.get(name) - 1;
        return DURATION_CONFIG.get(i).get();
    }

    public static int getEnergy(@NotNull String name) {
        int i = MiscUtil.tierMap.get(name) - 1;
        return ENERGY_CONFIG.get(i).get();
    }

    public static List<? extends Float> getModifiersFromTypeAndName(String name, ModifierBE.ModifierType type) {
        if (!SPEC.isLoaded() || type.equals(ModifierBE.ModifierType.NULL)) {
            return List.of(1f, 1f, 1f);
        }

        int i = MiscUtil.tierMap.get(name) - 1;

        return switch (type) {
            case ENERGY -> ENERGY_MOD_CONFIG.get(i).get();
            case SPEED -> SPEED_MOD_CONFIG.get(i).get();
            case ITEM -> ITEM_MOD_CONFIG.get(i).get();
            default -> List.of(1f, 1f, 1f);
        };


    }
}
