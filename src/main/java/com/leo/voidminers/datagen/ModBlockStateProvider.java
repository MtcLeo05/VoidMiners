package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.item.CrystalSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoidMiners.MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(
            ModBlocks.FRAME_BASE
        );

        simpleBlockWithItem(
            ModBlocks.STRUCTURE_PANEL
        );

        simpleAllCubeWithItem(
            ModBlocks.GLASS_PANEL
        );

        simpleAllCubeWithItem(ModBlocks.NULL_MOD);

        for (CrystalSet set : CrystalSet.sets()) {
            simpleAllCubeWithItem(
                set.CRYSTAL_BLOCK,
                set.name
            );

            simpleAllCubeWithItem(
                set.FRAME,
                set.name
            );

            simpleBlockWithItem(
                set.MINER_CONTROLLER
            );

            simpleAllCubeWithItem(
                set.ENERGY_MOD,
                set.name
            );

            simpleAllCubeWithItem(
                set.SPEED_MOD,
                set.name
            );

            simpleAllCubeWithItem(
                set.ITEM_MOD,
                set.name
            );
        }
    }


    private void simpleAllCubeWithItem(RegistryObject<Block> block, String name) {
        simpleBlockWithItem(block.get(), models().cubeAll(name(block.get()), stripSetName(block.getId()).withPrefix("block/" + name + "/")));
    }

    private void simpleAllCubeWithItem(RegistryObject<? extends Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }
    private void simpleBlockWithItem(RegistryObject<? extends Block> block) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(new ResourceLocation(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private void simpleBlockItem(RegistryObject<? extends Block> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(new ResourceLocation(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private static ResourceLocation texture(RegistryObject<? extends Block> block) {
        return texture(block.getId().getPath());
    }

    private static ResourceLocation texture(RegistryObject<? extends Block> block, String prefix) {
        return texture(stripSetName(block.getId()).withPrefix(prefix + "/").getPath());
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(VoidMiners.MODID, "block/" + name);
    }

    private static ModelFile model(RegistryObject<? extends Block> block) {
        return model(texture(block));
    }

    private static ModelFile model(ResourceLocation model) {
        return new ModelFile.UncheckedModelFile(model);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private static ResourceLocation stripSetName(ResourceLocation name) {
        int index = name.getPath().indexOf('_');

        if (index == -1) {
            return name;
        }

        return new ResourceLocation(name.getNamespace(), name.getPath().substring(index + 1));
    }

}
