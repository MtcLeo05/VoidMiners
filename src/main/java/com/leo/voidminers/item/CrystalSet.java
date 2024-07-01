package com.leo.voidminers.item;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.init.ModRarities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class CrystalSet {

    public static CrystalSet RUBETINE;
    public static CrystalSet AURANTIUM;
    public static CrystalSet CITRINETINE;

    public final String name;
    public final RegistryObject<Item> CRYSTAL;
    public final RegistryObject<Block> CRYSTAL_BLOCK;
    public final RegistryObject<Block> MINER_CONTROLLER;
    public final RegistryObject<Block> FRAME;
    public final RegistryObject<Block> SPEED_MOD;
    public final RegistryObject<Block> ENERGY_MOD;
    public final RegistryObject<Block> ITEM_MOD;

    CrystalSet(String name, RegistryObject<Item> crystal, RegistryObject<Block> crystalBlock, RegistryObject<Block> minerController, RegistryObject<Block> frame, RegistryObject<Block> energyMod, RegistryObject<Block> speedMod, RegistryObject<Block> itemMod) {
        this.name = name;
        CRYSTAL = crystal;
        CRYSTAL_BLOCK = crystalBlock;
        MINER_CONTROLLER = minerController;
        FRAME = frame;
        SPEED_MOD = speedMod;
        ENERGY_MOD = energyMod;
        ITEM_MOD = itemMod;
    }

    public static RegistryObject<Item> fastCreateItem(String name, Rarity rarity) {
        return ModItems.ITEMS.register(name, () -> new Item(new Item.Properties().rarity(rarity)));
    }

    public static RegistryObject<Block> fastCreateBlock(String name, float hardness, float resistance, Rarity rarity) {
        return ModBlocks.registerBlock(name,
            () -> new Block(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops()
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateModifier(String name, float hardness, float resistance, Rarity rarity, float energy, float speed, float item) {
        return ModBlocks.registerBlock(name,
            () -> new ModifierBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                new float[]{energy, speed, item}
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure, int duration, int rfTick) {
        return ModBlocks.registerBlock(name,
            () -> new ControllerBaseBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                structure,
                duration,
                rfTick
            ),
            rarity
        );
    }

    public static void initSets() {
        RUBETINE = createSet("rubetine", ModRarities.RUBETINE, 2500, 300, new float[]{0.90f, 1f, 1f}, new float[]{1.1f, 0.95f, 1f}, new float[]{1.2f, 1f, 1.75f});
        AURANTIUM = createSet("aurantium", ModRarities.AURANTIUM, 2250, 350, new float[]{0.80f, 1f, 1f}, new float[]{1.2f, 0.9f, 1f}, new float[]{1.3f, 1f, 2f});
        CITRINETINE = createSet("citrinetine", ModRarities.CITRINETINE, 2150, 375, new float[]{0.70f, 1f, 1f}, new float[]{1.3f, 0.85f, 1f}, new float[]{1.4f, 1f, 2.25f});
    }

    public static CrystalSet createSet(String name, Rarity rarity, int duration, int rfTick, float[] energy, float[] speed, float[] item) {
        return new CrystalSet(
            name,
            fastCreateItem(name, rarity),
            fastCreateBlock(name + "_block", 10, 5, rarity),
            fastCreateController(name + "_miner", 10, 50, rarity, new ResourceLocation(VoidMiners.MODID, name), duration, rfTick),
            fastCreateBlock(name + "_frame", 10, 50, rarity),
            fastCreateModifier(name + "_energy_modifier", 10, 50, rarity, energy[0], energy[1], energy[2]),
            fastCreateModifier(name + "_speed_modifier", 10, 50, rarity, speed[0], speed[1], speed[2]),
            fastCreateModifier(name + "_item_modifier", 10, 50, rarity, item[0], item[1], item[2])
        );
    }

    public static List<CrystalSet> getAllSets() {
        List<CrystalSet> sets = new ArrayList<>();

        sets.add(RUBETINE);
        sets.add(AURANTIUM);
        sets.add(CITRINETINE);

        return sets;
    }
}
