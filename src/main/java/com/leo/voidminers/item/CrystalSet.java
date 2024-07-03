package com.leo.voidminers.item;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.block.entity.ModifierBE;
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

    public static RegistryObject<Block> fastCreateModifier(String name, float hardness, float resistance, Rarity rarity, ModifierBE.ModifierType type) {
        return ModBlocks.registerBlock(name + "_" + type.type + "_modifier",
            () -> new ModifierBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                name,
                type
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure) {
        return ModBlocks.registerBlock(name + "_miner",
            () -> new ControllerBaseBlock(
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
    }

    public static CrystalSet createSet(String name, Rarity rarity) {
        return new CrystalSet(
            name,
            fastCreateItem(name, rarity),
            fastCreateBlock(name + "_block", 10, 5, rarity),
            fastCreateController(name, 10, 50, rarity, new ResourceLocation(VoidMiners.MODID, name)),
            fastCreateBlock(name + "_frame", 10, 50, rarity),
            fastCreateModifier(name, 10, 50, rarity, ModifierBE.ModifierType.ENERGY),
            fastCreateModifier(name, 10, 50, rarity, ModifierBE.ModifierType.SPEED),
            fastCreateModifier(name, 10, 50, rarity, ModifierBE.ModifierType.ITEM)
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
