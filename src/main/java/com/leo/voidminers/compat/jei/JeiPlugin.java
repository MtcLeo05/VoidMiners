package com.leo.voidminers.compat.jei;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.config.CommonConfig;
import com.leo.voidminers.config.CommonConfig;
import com.leo.voidminers.item.CrystalSet;
import com.leo.voidminers.recipe.JeiRecipe;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import com.leo.voidminers.util.ListUtil;
import com.leo.voidminers.util.MiscUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
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

        List<CrystalSet> allSets = CrystalSet.getAllSets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);
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
        List<MinerRecipe> foundRecipes = recipes.stream().filter(
            recipe -> {
                if(CommonConfig.shouldMinePreviousTiers()){
                    return recipe.getMinTier() <= tier + 1;
                } else {
                    return recipe.getMinTier() == tier + 1;
                }
            }
        ).toList();

        List<JeiRecipe> jeiRecipes = new ArrayList<>();
        for (MinerRecipe recipe : foundRecipes) {
            for (WeightedStack output : recipe.getOutputs()) {
                jeiRecipes.add(
                    new JeiRecipe(
                        output,
                        recipe.getDimension(),
                        ListUtil.getMaxWeightForDimension(foundRecipes).getOrDefault(recipe.getDimension(), 0f)
                    )
                );
            }
        }

        registration.addRecipes(
            tiers.get(tier).getRecipeType(),
            jeiRecipes
        );
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<CrystalSet> allSets = CrystalSet.getAllSets();
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
