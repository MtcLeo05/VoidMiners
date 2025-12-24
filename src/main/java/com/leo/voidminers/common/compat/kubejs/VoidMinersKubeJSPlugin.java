package com.leo.voidminers.common.compat.kubejs;

import com.leo.voidminers.VoidMiners;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import net.minecraft.resources.ResourceLocation;

public class VoidMinersKubeJSPlugin implements KubeJSPlugin {
    
    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow("com.leo.voidminers.common.compat.kubejs");
    }
    
    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {
        event.register(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "miner"), VoidMinerRecipeSchema.SCHEMA);
    }
}