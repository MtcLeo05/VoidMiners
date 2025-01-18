package com.leo.voidminers.compat.jei;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.item.CrystalSet;
import com.leo.voidminers.recipe.MinerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    List<MinerCategory> tiers = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(VoidMiners.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        List<CrystalSet> sets = CrystalSet.sets();
        for (int i = 0; i < sets.size(); i++) {
            CrystalSet set = sets.get(i);
            tiers.add(
                new MinerCategory(
                    registration.getJeiHelpers().getGuiHelper(),
                    set.MINER_CONTROLLER.get(),
                    i + 1
                )
            );
        }

        registration.addRecipeCategories(
            tiers.toArray(new MinerCategory[]{})
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<MinerRecipe> minerRecipes = manager.getAllRecipesFor(MinerRecipe.Type.INSTANCE);

        for (int i = 0; i < tiers.size(); i++) {
            addRecipeToTier(i, minerRecipes, registration);
        }
    }

    public void addRecipeToTier(int tier, List<MinerRecipe> recipes, IRecipeRegistration registration) {

        Comparator<MinerRecipe> alphabetical = Comparator.comparing(MinerRecipe::dimension);
        Comparator<MinerRecipe> weight = Comparator.comparing((recipe) -> recipe.output().weight);
        weight = weight.reversed();

        List<MinerRecipe> foundRecipes = recipes.stream().filter(
            recipe -> {
                if(ConfigLoader.getInstance().MINE_PREVIOUS_TIER){
                    return recipe.minTier() <= tier + 1;
                } else {
                    return recipe.minTier() == tier + 1;
                }
            }
        ).sorted(
            alphabetical.thenComparing(weight))
            .toList();

        registration.addRecipes(
            tiers.get(tier).getRecipeType(),
            foundRecipes
        );
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<CrystalSet> allSets = CrystalSet.sets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);
            registration.addRecipeCatalyst(
                set.MINER_CONTROLLER.get().asItem().getDefaultInstance(),
                tiers.get(i).getRecipeType()
            );
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }
}
