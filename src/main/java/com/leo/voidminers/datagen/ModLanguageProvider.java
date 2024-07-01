package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.item.CrystalSet;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, VoidMiners.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        this.add(VoidMiners.MODID + ".itemGroup.items", "Items");

        this.add(ModItems.ASSEMBLER.get(), "Structure Helper");

        this.add(VoidMiners.MODID + ".gui.miner", "Tier %d Miner");

        this.add(
            VoidMiners.MODID + ".controller.working", "Miner is working correctly."
        );

        this.add(
            VoidMiners.MODID + ".controller.energy", "Energy: %d rf/t"
        );

        this.add(
            VoidMiners.MODID + ".controller.duration", "Duration: %d ticks"
        );

        this.add(
            VoidMiners.MODID + ".controller.notWorking", "Miner is not working correctly! Check if the miner can see bedrock or the void"
        );

        this.add(
            VoidMiners.MODID + ".controller.missingStructure", "Structure not found, shift r-click the block for a guide. Total needed blocks: "
        );

        this.add(
            VoidMiners.MODID + ".structure.chance", "Chance: %d"
        );

        this.add(
            ModBlocks.FRAME_BASE.get(), "Frame Base"
        );

        this.add(
            ModBlocks.STRUCTURE_PANEL.get(), "Structure Panel"
        );

        this.add(
            ModBlocks.GLASS_PANEL.get(), "Glass Panel"
        );

        this.add(
            ModBlocks.NULL_MOD.get(), "Null Modifier"
        );

        for (CrystalSet set : CrystalSet.getAllSets()) {
            this.add(
                set.CRYSTAL.get(), cFL(set.name)
            );

            this.add(
                set.CRYSTAL_BLOCK.get(), cFL(set.name) + " Block"
            );

            this.add(
                set.MINER_CONTROLLER.get(), cFL(set.name) + " Miner"
            );

            this.add(
                set.FRAME.get(), cFL(set.name) + " Frame"
            );

            this.add(
                set.ENERGY_MOD.get(), cFL(set.name) + " Energy Modifier"
            );

            this.add(
                set.SPEED_MOD.get(), cFL(set.name) + " Speed Modifier"
            );

            this.add(
                set.ITEM_MOD.get(), cFL(set.name) + " Item Modifier"
            );
        }


        this.add(
            VoidMiners.MODID + ".tooltip.energy", "Energy Modifier: %dx"
        );

        this.add(
            VoidMiners.MODID + ".tooltip.speed", "Duration Modifier: %dx"
        );

        this.add(
            VoidMiners.MODID + ".tooltip.item", "Item Amount Modifier: %dx"
        );
    }


    /**
     * Capitalizes first letter of a string
     *
     * @param input the string to capitalize e.g. "alpha"
     * @return the string capitalized e.g. "Alpha"
     */
    public static String cFL(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}