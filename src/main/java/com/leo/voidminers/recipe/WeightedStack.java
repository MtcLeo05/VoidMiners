package com.leo.voidminers.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WeightedStack {
    public ItemStack stack;
    public float weight;

    public WeightedStack(ItemStack stack, float weight) {
        this.stack = stack;
        this.weight = weight;
    }

    public WeightedStack(Item item, float weight) {
        this.stack = item.getDefaultInstance();
        this.weight = weight;
    }
}
