package com.leo.voidminers.multiblock;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.datagen.ModBlockTagGenerator;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.item.CrystalSet;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MinerMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMiners.MODID, "miners");

    public static final SimpleMultiBlockAislePatternBuilder RUBETINE = createAccessiblePattern(
        VoidMiners.MODID + ":rubetine",
        List.of(
            List.of(
                "     ", "     ", "  *  ", "     ", "     "
            ),
            List.of(
                "     ", "  F  ", " F F ", "  F  ", "     "
            ),
            List.of(
                "  F  ", "     ", "F   F", "     ", "  F  "
            ),
            List.of(
                " FFF ", "FMPMF", "FPPPF", "FMPMF", " FFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.RUBETINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_1),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.RUBETINE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder AURANTIUM = createAccessiblePattern(
        VoidMiners.MODID + ":aurantium",
        List.of(
            List.of(
                "       ", "       ", "       ", "   *   ", "       ", "       ", "       "
            ),
            List.of(
                "       ", "   F   ", "   F   ", " FF FF ", "   F   ", "   F   ", "       "
            ),
            List.of(
                "   F   ", "       ", "       ", "F     F", "       ", "       ", "   F   "
            ),
            List.of(
                "   F   ", "       ", "       ", "F     F", "       ", "       ", "   F   "
            ),
            List.of(
                " FFFFF ", "FMPMPMF", "FPPPPPF", "FPPPPPF", "FPPPPPF", "FMPMPMF", " FFFFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.AURANTIUM.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_2),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.AURANTIUM.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder CITRINETINE = createAccessiblePattern(
        VoidMiners.MODID + ":citrinetine",
        List.of(
            List.of(
                "       ", "       ", "       ", "   *   ", "       ", "       ", "       "
            ),
            List.of(
                "       ", "       ", "   F   ", "  F F  ", "   F   ", "       ", "       "
            ),
            List.of(
                "       ", "   F   ", "       ", " F   F ", "       ", "   F   ", "       "
            ),
            List.of(
                "   F   ", "       ", "       ", "F     F", "       ", "       ", "   F   "
            ),
            List.of(
                "   F   ", "       ", "       ", "F     F", "       ", "       ", "   F   "
            ),
            List.of(
                " FFFFF ", "FMPMPMF", "FPPPPPF", "FMPPPMF", "FPPPPPF", "FMPMPMF", " FFFFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.CITRINETINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_3),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.CITRINETINE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder VERDIUM =
        createAccessiblePattern(
            VoidMiners.MODID + ":verdium",
            List.of(
                List.of(
                    "         ", "         ","         ","         ","    *    ","         ","         ","         ","         "
                ),
                List.of(
                    "         ", "         ","    F    ","    F    ","  FF FF  ","    F    ","    F    ","         ","         "
                ),
                List.of(
                    "         ", "    F    ","         ","         "," F     F ","         ","         ","    F    ","         "
                ),
                List.of(
                    "    F    ", "         ","         ","         ","F       F","         ","         ","         ","    F    "
                ),
                List.of(
                    "    F    ", "         ","         ","         ","F       F","         ","         ","         ","    F    "
                ),
                List.of(
                    "  FFFFF  ", " FMMPMMF ","FMMPPPMMF","FMPPPPPMF","FPPPPPPPF","FMPPPPPMF","FMMPPPMMF"," FMMPMMF ","  FFFFF  "
                )
            ),
            Map.of(
                '*', a -> a.getState().is(CrystalSet.VERDIUM.MINER_CONTROLLER.get()),
                'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_4),
                'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                'F', CrystalSet.VERDIUM.FRAME.get()::defaultBlockState,
                'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
        );


    //TODO remove this hack when a better way to access the blocks is found
    public static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(String structure, List<List<String>> stringPattern, Map<Character, Predicate<BlockInWorld>> lookup, Map<Character, Supplier<BlockState>> blockProvider) {
        SimpleMultiBlockAislePatternBuilder pattern = SimpleMultiBlockAislePatternBuilder.start();
        List<List<List<BlockState>>> blocks = new ArrayList<>();

        for (List<String> strings : stringPattern) {
            pattern.aisle(strings.toArray(new String[]{}));

            List<List<BlockState>> blockForAisle = new ArrayList<>();

            for (String s : strings) {
                blockForAisle.add(
                    getStatesForString(s, blockProvider)
                );
            }

            blocks.add(blockForAisle);
        }

        MiscUtil.structureMap.put(
            structure,
            blocks
        );

        lookup.forEach(pattern::where);

        blockProvider.forEach(pattern::block);

        return pattern;
    }

    private static List<BlockState> getStatesForString(String s, Map<Character, Supplier<BlockState>> map) {
        List<BlockState> toReturn = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                toReturn.add(
                    Blocks.AIR.defaultBlockState()
                );
            } else {
                if (map.containsKey(c)) {
                    toReturn.add(
                        map.get(c).get()
                    );
                }
            }


        }

        return toReturn;
    }


    public static void init() {
        MANAGER.register("rubetine", RUBETINE.build());
        MANAGER.register("aurantium", AURANTIUM.build());
        MANAGER.register("citrinetine", CITRINETINE.build());
        MANAGER.register("verdium", VERDIUM.build());
    }

}
