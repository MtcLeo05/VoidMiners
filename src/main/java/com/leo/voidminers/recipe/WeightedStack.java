package com.leo.voidminers.recipe;

import net.minecraft.world.item.ItemStack;

public class WeightedStack {

    public ItemStack stack;
    public float weight;

    public WeightedStack(ItemStack stack, float weight) {
        this.stack = stack;
        this.weight = weight;
    }

    public WeightedStack(float weight, ItemStack stack) {
        this.stack = stack;
        this.weight = weight;
    }

}
