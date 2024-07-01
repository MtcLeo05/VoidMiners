package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.item.CrystalSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoidMiners.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.ASSEMBLER);

        for (CrystalSet set : CrystalSet.getAllSets()) {
            simpleItem(
                set.CRYSTAL
            );
        }
    }

    private void simpleItem(RegistryObject<? extends Item> item) {
        simpleItem(item.getId().getPath());
    }

    private void simpleItem(String name) {
        withExistingParent(name,
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(VoidMiners.MODID, "item/" + name));
    }
}
