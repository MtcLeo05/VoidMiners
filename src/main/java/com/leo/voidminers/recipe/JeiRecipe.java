package com.leo.voidminers.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class JeiRecipe implements Recipe<Container> {

    private final WeightedStack output;
    private final ResourceKey<Level> dimension;
    private final float totalWeight;

    public JeiRecipe(WeightedStack output, ResourceKey<Level> dimension, float totalWeight) {
        this.output = output;
        this.dimension = dimension;
        this.totalWeight = totalWeight;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return output.stack;
    }

    public WeightedStack getOutput() {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.stack;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<JeiRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "output";
    }
}
