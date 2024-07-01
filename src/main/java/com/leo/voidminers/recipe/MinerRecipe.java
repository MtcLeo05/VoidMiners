package com.leo.voidminers.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leo.voidminers.VoidMiners;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MinerRecipe implements Recipe<Container> {
    private final List<WeightedStack> outputs;
    private final int minTier;
    private final ResourceLocation id;
    private final ResourceKey<Level> dimension;

    public MinerRecipe(List<WeightedStack> outputs, int minTier, ResourceLocation id, ResourceKey<Level> dimension) {
        this.outputs = outputs;
        this.minTier = minTier;
        this.id = id;
        this.dimension = dimension;
    }

    public static MinerRecipe.Builder create(List<WeightedStack> outputs, int minTier, ResourceKey<Level> dimension) {
        ResourceLocation recipeId = new ResourceLocation(VoidMiners.MODID, "tier" + minTier + "_miner" + "/" + dimension.location().getPath());
        return new MinerRecipe.Builder(outputs, minTier, recipeId, dimension);
    }

    public List<WeightedStack> getOutputs() {
        return outputs;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public int getMinTier() {
        return minTier;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return !pLevel.isClientSide();
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).stack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MinerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "miner";
    }

    public static class Serializer implements RecipeSerializer<MinerRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MinerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            List<WeightedStack> outputs = new ArrayList<>();
            JsonArray jsonOutputs = GsonHelper.getAsJsonArray(pSerializedRecipe, "outputs");

            for (JsonElement output : jsonOutputs) {
                JsonObject object = output.getAsJsonObject();

                ItemStack stack = CraftingHelper.getItemStack(object, true, true);
                outputs.add(
                    new WeightedStack(
                        stack,
                        GsonHelper.getAsFloat(object, "weight", 1)
                    )
                );
            }

            int minTier = GsonHelper.getAsInt(pSerializedRecipe, "minTier");

            String jsonDim = GsonHelper.getAsString(pSerializedRecipe, "dimension", "minecraft:overworld");

            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(jsonDim));

            return new MinerRecipe(outputs, minTier, pRecipeId, dimension);
        }

        @Override
        public @Nullable MinerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            List<WeightedStack> outputs = new ArrayList<>();

            int outputAmount = pBuffer.readInt();

            for (int i = 0; i < outputAmount; i++) {
                ItemStack stack = pBuffer.readItem();
                float weight = pBuffer.readFloat();

                outputs.add(
                    new WeightedStack(
                        stack,
                        weight
                    )
                );
            }

            int minTier = pBuffer.readInt();

            ResourceKey<Level> dimension = pBuffer.readResourceKey(Registries.DIMENSION);

            return new MinerRecipe(outputs, minTier, pRecipeId, dimension);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MinerRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.outputs.size());

            pRecipe.outputs.forEach(stack -> {
                pBuffer.writeItem(stack.stack);
                pBuffer.writeFloat(stack.weight);
            });

            pBuffer.writeInt(pRecipe.minTier);

            pBuffer.writeResourceKey(pRecipe.dimension);
        }
    }

    public static class Builder implements RecipeBuilder, FinishedRecipe {
        private final List<WeightedStack> outputs;
        private final int minTier;
        private final ResourceLocation id;
        private final ResourceKey<Level> dimension;

        private Builder(List<WeightedStack> outputs, int minTier, ResourceLocation id, ResourceKey<Level> dimension) {
            this.outputs = outputs;
            this.minTier = minTier;
            this.id = id;
            this.dimension = dimension;
        }

        @Override
        public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String pGroupName) {
            return this;
        }

        @Override
        public @NotNull Item getResult() {
            return this.outputs.get(0).stack.getItem();
        }

        @Override
        public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
            finishedRecipeConsumer.accept(this);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray output = new JsonArray();
            for (WeightedStack itemOutput : this.outputs) {
                JsonObject outputItem = new JsonObject();
                outputItem.addProperty("item", ForgeRegistries.ITEMS.getKey(itemOutput.stack.getItem()).toString());
                if (itemOutput.stack.getCount() != 1) {
                    outputItem.addProperty("count", itemOutput.stack.getCount());
                }
                if (itemOutput.stack.getTag() != null) {
                    outputItem.addProperty("nbt", itemOutput.stack.getTag().toString());
                }

                outputItem.addProperty("weight", itemOutput.weight);
                output.add(outputItem);
            }

            json.add("outputs", output);

            json.addProperty("minTier", this.minTier);
            json.addProperty("dimension", dimension.location().toString());
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
