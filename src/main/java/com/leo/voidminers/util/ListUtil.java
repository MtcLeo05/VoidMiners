package com.leo.voidminers.util;

import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil {

    public static List<WeightedStack> getAllItemsForTier(List<MinerRecipe> recipes, int tier) {
        List<WeightedStack> allItems = new ArrayList<>();

        recipes
            .stream()
            .filter(recipe -> recipe.getMinTier() <= tier)
            .forEach(recipe -> {
                allItems.addAll(
                    recipe.getOutputs()
                );
            });

        return allItems;
    }

    public static List<MinerRecipe> getAllRecipesForTier(List<MinerRecipe> recipes, int tier) {
        return recipes.stream().filter(recipe -> recipe.getMinTier() <= tier).toList();
    }

    public static float getTotalWeight(List<WeightedStack> items) {
        float totalWeight = 0;

        for (WeightedStack item : items) {
            totalWeight += item.weight;
        }

        return totalWeight;
    }

    public static Map<ResourceKey<Level>, Float> getMaxWeightForDimension(List<MinerRecipe> recipes) {
        Map<ResourceKey<Level>, Float> toReturn = new HashMap<>();

        for (MinerRecipe recipe : recipes) {
            for (WeightedStack output : recipe.getOutputs()) {
                if (!toReturn.containsKey(recipe.getDimension())) {
                    toReturn.put(recipe.getDimension(), output.weight);
                } else {
                    toReturn.compute(recipe.getDimension(), (k, f) -> f + output.weight);
                }
            }
        }

        return toReturn;
    }

}
