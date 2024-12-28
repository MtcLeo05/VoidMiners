package com.leo.voidminers.util;

import com.leo.voidminers.recipe.WeightedStack;

import java.util.List;

public class ListUtil {
    public static float getTotalWeight(List<WeightedStack> items) {
        float totalWeight = 0;

        for (WeightedStack item : items) {
            totalWeight += item.weight;
        }

        return totalWeight;
    }
}
