package nadiendev.voidminers.init;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.world.item.StructureHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(Registries.ITEM, VoidMiners.MODID);

    public static final DeferredHolder<Item, StructureHelper> STRUCTURE_HELPER = ITEMS.register("structure_helper",
        () -> new StructureHelper(new Item.Properties())
    );
}