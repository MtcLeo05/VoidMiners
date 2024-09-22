package com.leo.voidminers.util;

import com.leo.voidminers.item.CrystalSet;
import net.minecraft.locale.Language;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    public static Map<String, Integer> tierMap = new HashMap<>();
    public static Map<String, Integer> colorMap = new HashMap<>();

    public static Map<String, List<List<List<BlockState>>>> structureMap = new HashMap<>();

    static {
        for (int i = 0; i < CrystalSet.getAllSets().size(); i++) {
            CrystalSet set = CrystalSet.getAllSets().get(i);

            tierMap.put(
                set.name,
                i + 1
            );
        }

        colorMap.put(
            "rubetine",
            0xFFFF0000
        );

        colorMap.put(
            "aurantium",
            0xFFFFAA00
        );

        colorMap.put(
            "citrinetine",
            0xFFFFFF00
        );

        colorMap.put(
            "verdium",
            0xFF00FF00
        );
    }


    public static Map<String, Integer> getNeededBlocks(List<List<List<BlockState>>> structure) {
        Map<String, Integer> blocks = new HashMap<>();

        for (List<List<BlockState>> l1 : structure) {
            for (List<BlockState> l2 : l1) {
                for (BlockState state : l2) {
                    if (!state.getBlock().equals(Blocks.AIR)) {
                        String name = Language.getInstance().getOrDefault(state.getBlock().getDescriptionId());

                        if (!blocks.containsKey(name)) {
                            blocks.put(name, 1);
                        } else {
                            blocks.compute(name, (k, i) -> i + 1);
                        }
                    }
                }
            }
        }

        return blocks;
    }
}
