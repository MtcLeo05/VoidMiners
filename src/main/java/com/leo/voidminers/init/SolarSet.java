package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.SolarControllerBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class SolarSet {

    public static SolarSet RUBETINE;
    public static SolarSet AURANTIUM;
    public static SolarSet CITRINETINE;
    public static SolarSet VERDIUM;
    public static SolarSet AZURINE;
    public static SolarSet CAERIUM;
    public static SolarSet AMETHYSTINE;
    public static SolarSet ROSARIUM;

    public final String name;
    public final RegistryObject<Block> SOLAR_CONTROLLER;

    SolarSet(String name, RegistryObject<Block> solarController) {
        this.name = name;
        this.SOLAR_CONTROLLER = solarController;
    }

    public static RegistryObject<Block> fastCreateSolarController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure) {
        return ModBlocks.registerBlock(name + "_solar_panel",
            () -> new SolarControllerBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                structure,
                name
            ),
            rarity
        );
    }

    public static void initSets() {
        RUBETINE = createSet("rubetine", ModRarities.RUBETINE);
        AURANTIUM = createSet("aurantium", ModRarities.AURANTIUM);
        CITRINETINE = createSet("citrinetine", ModRarities.CITRINETINE);
        VERDIUM = createSet("verdium", ModRarities.VERDIUM);
        AZURINE = createSet("azurine", ModRarities.AZURINE);
        CAERIUM = createSet("caerium", ModRarities.CAERIUM);
        AMETHYSTINE = createSet("amethystine", ModRarities.AMETHYSTINE);
        ROSARIUM = createSet("rosarium", ModRarities.ROSARIUM);
    }

    public static SolarSet createSet(String name, Rarity rarity) {
        return new SolarSet(
            name,
            fastCreateSolarController(name, 10, 50, rarity, ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, name + "_solar"))
        );
    }

    public static List<SolarSet> sets() {
        List<SolarSet> sets = new ArrayList<>();
        sets.add(RUBETINE);
        sets.add(AURANTIUM);
        sets.add(CITRINETINE);
        sets.add(VERDIUM);
        sets.add(AZURINE);
        sets.add(CAERIUM);
        sets.add(AMETHYSTINE);
        sets.add(ROSARIUM);
        return sets;
    }
}
