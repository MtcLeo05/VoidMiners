package nadiendev.voidminers.common.compat.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;

public interface VoidMinerRecipeSchema {
    
    RecipeKey<ItemStack> ITEM = ItemStackComponent.ITEM_STACK
        .key("item", ComponentRole.OUTPUT)
        .noFunctions();
    
    RecipeKey<String> DIMENSION = StringComponent.STRING
        .key("dimension", ComponentRole.INPUT)
        .noFunctions();
    
    RecipeKey<Integer> MIN_TIER = NumberComponent.INT
        .key("minTier", ComponentRole.INPUT)
        .noFunctions();
    
    RecipeKey<Double> WEIGHT = NumberComponent.DOUBLE
        .key("weight", ComponentRole.OTHER)
        .optional(1.0)
        .alwaysWrite();
    
    RecipeKey<Integer> COUNT = NumberComponent.INT
        .key("count", ComponentRole.OTHER)
        .optional(1)
        .alwaysWrite();
    
    RecipeKey<Boolean> ALLOW_HIGHER_TIERS = BooleanComponent.BOOLEAN
        .key("allowHigherTiers", ComponentRole.OTHER)
        .optional(true)
        .alwaysWrite();
    
    RecipeSchema SCHEMA = new RecipeSchema(ITEM, DIMENSION, MIN_TIER, WEIGHT, COUNT, ALLOW_HIGHER_TIERS);
}