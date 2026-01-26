package nadiendev.voidminers.init;

import nadiendev.voidminers.VoidMiners;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VoidMiners.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = 
        CREATIVE_MODE_TABS.register("items", () ->
            CreativeModeTab.builder()
                .title(Component.translatable(VoidMiners.MODID + ".itemGroup.items"))
                .icon(() -> new ItemStack(ModItems.STRUCTURE_HELPER.get()))
                .displayItems((parameters, output) -> {
         
                    ModItems.ITEMS.getEntries().forEach(entry -> {
                        output.accept(entry.get());
                    });

            
                    ModBlocks.BLOCKS.getEntries().forEach(entry -> {
                        output.accept(entry.get());
                    });
                })
                .build()
        );
}