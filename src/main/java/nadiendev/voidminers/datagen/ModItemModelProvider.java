package nadiendev.voidminers.datagen;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.init.ModItems;
import nadiendev.voidminers.init.CrystalSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoidMiners.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.STRUCTURE_HELPER);

        for (CrystalSet set : CrystalSet.sets()) {
            simpleItem(
                set.CRYSTAL
            );
        }
    }

    private void simpleItem(DeferredHolder<Item, ? extends Item> item) {
        simpleItem(item.getId().getPath());
    }

    private void simpleItem(String name) {
        withExistingParent(name,
            ResourceLocation.parse("item/generated")).texture("layer0",
            ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "item/" + name));
    }
}