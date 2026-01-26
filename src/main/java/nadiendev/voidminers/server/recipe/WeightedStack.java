package nadiendev.voidminers.server.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public WeightedStack copy() {
        return new WeightedStack(stack.copy(), weight);
    }

 
    public static final Codec<WeightedStack> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(ws -> ws.stack),
            Codec.FLOAT.fieldOf("weight").forGetter(ws -> ws.weight)
        ).apply(instance, WeightedStack::new)
    );

    
    public static final StreamCodec<RegistryFriendlyByteBuf, WeightedStack> STREAM_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC,
        ws -> ws.stack,
        ByteBufCodecs.FLOAT,
        ws -> ws.weight,
        WeightedStack::new
    );
}