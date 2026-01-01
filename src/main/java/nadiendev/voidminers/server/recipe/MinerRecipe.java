package nadiendev.voidminers.server.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nadiendev.voidminers.VoidMiners;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class MinerRecipe implements Recipe<RecipeInput> {
    private final WeightedStack output;
    private final int minTier;
    private final boolean allowHigherTiers;
    private final ResourceKey<Level> dimension;

    public MinerRecipe(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension) {
        this.output = output;
        this.minTier = minTier;
        this.allowHigherTiers = allowHigherTiers;
        this.dimension = dimension;
    }

    public boolean allowHigherTiers() {
        return allowHigherTiers;
    }

    public WeightedStack output() {
        return output.copy();
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    public int minTier() {
        return minTier;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        return !pLevel.isClientSide();
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output.stack.isEmpty() ? ItemStack.EMPTY : output.stack;
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

        private static final MapCodec<MinerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> 
            instance.group(
                WeightedStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                Codec.INT.fieldOf("minTier").forGetter(recipe -> recipe.minTier),
                Codec.BOOL.optionalFieldOf("allowHigherTiers", true).forGetter(recipe -> recipe.allowHigherTiers),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(recipe -> recipe.dimension)
            ).apply(instance, MinerRecipe::new)
        );
        
        private static final MapCodec<MinerRecipe> KUBEJS_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("item").forGetter(recipe -> recipe.output.stack),
                Codec.INT.optionalFieldOf("count", 1).forGetter(recipe -> recipe.output.stack.getCount()),
                Codec.DOUBLE.optionalFieldOf("weight", 1.0).forGetter(recipe -> (double)recipe.output.weight),
                Codec.INT.fieldOf("minTier").forGetter(recipe -> recipe.minTier),
                Codec.BOOL.optionalFieldOf("allowHigherTiers", true).forGetter(recipe -> recipe.allowHigherTiers),
                ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension", Level.OVERWORLD).forGetter(recipe -> recipe.dimension)
            ).apply(instance, (item, count, weight, minTier, allowHigher, dim) -> {
                ItemStack stack = item.copy();
                stack.setCount(count);
                return new MinerRecipe(new WeightedStack(stack, weight.floatValue()), minTier, allowHigher, dim);
            })
        );
        
        private static final MapCodec<MinerRecipe> COMBINED_CODEC = new MapCodec<MinerRecipe>() {
            @Override
            public <T> RecordBuilder<T> encode(MinerRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return CODEC.encode(input, ops, prefix);
            }

            @Override
            public <T> DataResult<MinerRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                if (input.get("output") != null) {
                    return CODEC.decode(ops, input);
                } else if (input.get("item") != null) {
                    return KUBEJS_CODEC.decode(ops, input);
                }
                return DataResult.error(() -> "Recipe must have either 'output' or 'item' field");
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(CODEC.keys(ops), KUBEJS_CODEC.keys(ops));
            }
        };

        private static final StreamCodec<RegistryFriendlyByteBuf, MinerRecipe> STREAM_CODEC = StreamCodec.of(
            Serializer::toNetwork,
            Serializer::fromNetwork
        );

        @Override
        public MapCodec<MinerRecipe> codec() {
            return COMBINED_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MinerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MinerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            WeightedStack output = WeightedStack.STREAM_CODEC.decode(buffer);
            int minTier = buffer.readInt();
            boolean allowHigherTiers = buffer.readBoolean();
            ResourceKey<Level> dimension = buffer.readResourceKey(Registries.DIMENSION);

            return new MinerRecipe(output, minTier, allowHigherTiers, dimension);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, MinerRecipe recipe) {
            WeightedStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeInt(recipe.minTier);
            buffer.writeBoolean(recipe.allowHigherTiers);
            buffer.writeResourceKey(recipe.dimension);
        }
    }

    public static class Builder implements RecipeBuilder {
        private final WeightedStack output;
        private final int minTier;
        private final boolean allowHigherTiers;
        private final ResourceLocation id;
        private final ResourceKey<Level> dimension;

        private Builder(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceLocation id, ResourceKey<Level> dimension) {
            this.output = output;
            this.minTier = minTier;
            this.id = id;
            this.allowHigherTiers = allowHigherTiers;
            this.dimension = dimension;
        }

        public static Builder builder(WeightedStack output, int minTier, ResourceKey<Level> dimension) {
            return Builder.builder(output, minTier, true, dimension);
        }

        public static Builder builder(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension) {
            ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.stack.getItem());
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                VoidMiners.MODID, 
                dimension.location().getPath() + "/tier" + minTier + "_miner/" + itemId.getPath()
            );
            return new Builder(output, minTier, allowHigherTiers, recipeId, dimension);
        }

        @Override
        public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String pGroupName) {
            return this;
        }

        @Override
        public @NotNull Item getResult() {
            return this.output.stack.getItem();
        }

        @Override
        public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
            MinerRecipe recipe = new MinerRecipe(this.output, this.minTier, this.allowHigherTiers, this.dimension);
            pRecipeOutput.accept(pId, recipe, null);
        }

        public void save(RecipeOutput pRecipeOutput) {
            this.save(pRecipeOutput, this.id);
        }
    }
}