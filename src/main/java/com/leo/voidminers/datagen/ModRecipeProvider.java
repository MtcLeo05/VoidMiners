package com.leo.voidminers.datagen;

import com.leo.voidminers.item.CrystalSet;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        MinerRecipe.create(
            List.of(
                new WeightedStack(
                    Items.EMERALD_ORE.getDefaultInstance(),
                    1f
                ),
                new WeightedStack(
                    Items.DIAMOND_ORE.getDefaultInstance(),
                    2f
                ),
                new WeightedStack(
                    Items.GOLD_ORE.getDefaultInstance(),
                    4f
                ),
                new WeightedStack(
                    Items.REDSTONE_ORE.getDefaultInstance(),
                    6f
                ),
                new WeightedStack(
                    Items.LAPIS_ORE.getDefaultInstance(),
                    6f
                ),
                new WeightedStack(
                    Items.IRON_ORE.getDefaultInstance(),
                    8f
                ),
                new WeightedStack(
                    Items.COPPER_ORE.getDefaultInstance(),
                    12f
                ),
                new WeightedStack(
                    Items.COAL_ORE.getDefaultInstance(),
                    16f
                ),
                new WeightedStack(
                    CrystalSet.RUBETINE.CRYSTAL.get().getDefaultInstance(),
                    2f
                ),
                new WeightedStack(
                    CrystalSet.AURANTIUM.CRYSTAL.get().getDefaultInstance(),
                    2f
                )
            ),
            1,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.create(
            List.of(
                new WeightedStack(
                    Items.NETHER_QUARTZ_ORE.getDefaultInstance(),
                    10f
                ),
                new WeightedStack(
                    Items.NETHER_GOLD_ORE.getDefaultInstance(),
                    5f
                ),
                new WeightedStack(
                    Items.ANCIENT_DEBRIS.getDefaultInstance(),
                    0.1f
                ),
                new WeightedStack(
                    CrystalSet.RUBETINE.CRYSTAL.get().getDefaultInstance(),
                    2f
                ),
                new WeightedStack(
                    CrystalSet.AURANTIUM.CRYSTAL.get().getDefaultInstance(),
                    2f
                )
            ),
            1,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.create(
            List.of(
                new WeightedStack(
                    CrystalSet.CITRINETINE.CRYSTAL.get().getDefaultInstance(),
                    2f
                )
            ),
            2,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.create(
            List.of(
                new WeightedStack(
                    CrystalSet.CITRINETINE.CRYSTAL.get().getDefaultInstance(),
                    4f
                )
            ),
            2,
            Level.NETHER
        ).save(pWriter);
    }

}
