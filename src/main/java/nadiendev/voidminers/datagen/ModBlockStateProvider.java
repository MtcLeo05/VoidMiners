package nadiendev.voidminers.datagen;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.init.ModBlocks;
import nadiendev.voidminers.init.CrystalSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {

    ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoidMiners.MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockItem(ModBlocks.FRAME_BASE);

        quadLayerBlockWithItem(ModBlocks.FRAME_BASE, "voidminers:block/null/frame", "voidminers:block/_core/frame", "voidminers:block/_core/cover", "voidminers:block/null/core");

        dualLayerBlockWithItem(ModBlocks.STRUCTURE_PANEL, "voidminers:block/_core/panel", "voidminers:block/_core/cover");

        simpleBlockWithItem(ModBlocks.GLASS_PANEL);

        quadLayerBlockWithItem(ModBlocks.NULL_MOD, "voidminers:block/null/modifier", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/null/core");

        for (CrystalSet set : CrystalSet.sets()) {
            simpleAllCubeWithItem(
                set.CRYSTAL_BLOCK,
                set.name
            );

            tripleLayerBlockWithItem(set.FRAME, "voidminers:block/" + set.name + "/frame", "voidminers:block/_core/frame", "voidminers:block/_core/cover");

            simpleBlockWithItem(set.MINER_CONTROLLER);

            quadLayerBlockWithItem(set.ENERGY_MOD, "voidminers:block/_core/energy", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.SPEED_MOD, "voidminers:block/_core/speed", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.ITEM_MOD, "voidminers:block/_core/item", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");
        }
    }

    private void simpleBlockWithItem(DeferredHolder<Block, ? extends Block> block) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private void simpleBlockWithItem(DeferredHolder<Block, ? extends Block> block, String name) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + name)));
    }

    private void simpleBlockItem(DeferredHolder<Block, ? extends Block> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private void dualLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/dual_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1);

        simpleBlockWithItem(block.get(), model);
    }

    private void tripleLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1, String layer2) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/triple_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2);

        simpleBlockWithItem(block.get(), model);
    }

    private void quadLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1, String layer2, String layer3) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/quad_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2)
            .texture("layer3", layer3);

        simpleBlockWithItem(block.get(), model);
    }

    private void simpleAllCubeWithItem(DeferredHolder<Block, Block> block, String name) {
        simpleBlockWithItem(block.get(), models().cubeAll(name(block.get()), stripSetName(block.getId()).withPrefix("block/" + name + "/")));
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static ResourceLocation stripSetName(ResourceLocation name) {
        int index = name.getPath().indexOf('_');

        if (index == -1) {
            return name;
        }

        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath().substring(index + 1));
    }
}