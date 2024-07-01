package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.recipe.MinerRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VoidMiners.MODID);

    public static final RegistryObject<RecipeSerializer<MinerRecipe>> MINER_RECIPE_SERIALIZER =
        SERIALIZERS.register("miner", () -> MinerRecipe.Serializer.INSTANCE);
}
