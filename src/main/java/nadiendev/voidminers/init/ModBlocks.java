package nadiendev.voidminers.init;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.world.block.BaseTransparentBlock;
import nadiendev.voidminers.world.block.ModifierBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(Registries.BLOCK, VoidMiners.MODID);

    public static final DeferredHolder<Block, Block> FRAME_BASE = registerBlock("frame_base",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops()
        )
    );

    public static final DeferredHolder<Block, Block> STRUCTURE_PANEL = registerBlock("structure_panel",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops()
        )
    );

    public static final DeferredHolder<Block, BaseTransparentBlock> GLASS_PANEL = registerBlock("glass_panel",
        () -> new BaseTransparentBlock(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops()
                .sound(SoundType.GLASS)
        )
    );

    public static final DeferredHolder<Block, ModifierBlock> NULL_MOD = registerBlock("null_modifier",
        () -> new ModifierBlock(
            BlockBehaviour.Properties.of()
                .strength(10, 50)
                .requiresCorrectToolForDrops(),
            "null"
        )
    );

    public static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block, Rarity rarity) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block, Rarity rarity) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity)));
    }
}