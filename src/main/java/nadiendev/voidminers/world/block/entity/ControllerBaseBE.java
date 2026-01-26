package nadiendev.voidminers.world.block.entity;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.world.block.ModifierBlock;
import nadiendev.voidminers.config.ConfigLoader;
import nadiendev.voidminers.common.energy.ModEnergyStorage;
import nadiendev.voidminers.init.ModBlockEntities;
import nadiendev.voidminers.server.recipe.MinerRecipe;
import nadiendev.voidminers.server.recipe.WeightedStack;
import nadiendev.voidminers.util.ListUtil;
import nadiendev.voidminers.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerBaseBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = 1000000;

    private ModEnergyStorage energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (ControllerBaseBE.this.level != null) {
                ControllerBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, ConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    public boolean active;
    public boolean working;

    
    public ControllerBaseBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CONTROLLER_BASE_BE.get(), pPos, pBlockState);
    }
    
    
    public ControllerBaseBE(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public void setup(ResourceLocation structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        int storage = ConfigLoader.getInstance().getMinerConfig(name).energyStorage();

        if (!ConfigLoader.getInstance().ALLOW_NO_ENERGY_MINERS && storage <= 0) storage = ENERGY_CAPACITY;
        
        int currentEnergy = energyHandler != null ? energyHandler.getEnergyStored() : 0;
        energyHandler = new ModEnergyStorage(storage, storage, 0, currentEnergy);
    }

    public int getBeamColor() {
        return MiscUtil.colorMap.getOrDefault(structure.getPath(), 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> toRet = new ArrayList<>();

        if(working) {
            return List.of(Component.translatable("tooltip." + VoidMiners.MODID + ".controller.working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.energy", getRfTick()),
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.duration", getMaxProgress()));
        }

        if (active) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.not_working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.energy", getRfTick())
            );
        }

        if (foundStructure) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".controller.not_active")
            );
        }

        toRet.add(Component.translatable("tooltip." + VoidMiners.MODID + ".controller.missing_structure") );

        if (structure != null && MiscUtil.structureMap.containsKey(structure.toString())) {
            MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
                toRet.add(Component.literal(string + ": " + integer));
            });
        }

        return toRet;
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT(pRegistries));
        data.put("items", itemHandler.serializeNBT(pRegistries));
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        data.putBoolean("active", active);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        pTag.put(VoidMiners.MODID, data);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag data = pTag.getCompound(VoidMiners.MODID);
        if (data.isEmpty())
            return;

        if (data.contains("energy")) {
            energyHandler.deserializeNBT(pRegistries, data.get("energy"));
        }

        if (data.contains("items")) {
            itemHandler.deserializeNBT(pRegistries, data.getCompound("items"));
        }

        if (data.contains("progress")) {
            progress = data.getInt("progress");
        }

        if (data.contains("name")) {
            name = data.getString("name");
        }

        if (data.contains("active")) {
            active = data.getBoolean("active");
        }

        if (data.contains("structure")) {
            structure = ResourceLocation.parse(data.getString("structure"));
        }

        if (data.contains("showStructure")) {
            showStructure = data.getBoolean("showStructure");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setupEnergyStorage();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.handleUpdateTag(tag, pRegistries);
        this.loadAdditional(tag, pRegistries);
    }

    public ModEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // MÉTODO AGREGADO: Getter para progress
    public int getProgress() {
        return progress;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        if(getStructure() == null) {
            setup(structure, name);
        }

        checkStructure(pLevel, pPos);

        active = foundStructure && hasViewOnBedrockOrVoid(pPos);
        if (level != null) {
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if(!active) return;

        working = !isItemHandlerFull() && getRfTick() <= energyHandler.getEnergyStored();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        if (!working) {
            return;
        }

        progress++;
        energyHandler.removeEnergy(getRfTick());

        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        if (progress < getMaxProgress()) {
            return;
        }

        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : allRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        ItemStack output = getBoostedStack(getWeightedItem(allOutputs, level.random));
        ItemStack remaining;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!isItemValid(output, itemHandler.getStackInSlot(i))) continue;
            remaining = itemHandler.insertItem(i, output.copy(), false);
            if (remaining.isEmpty()) break;
            output = remaining;
        }

        progress = 0;
        sync();
    }

    private void sync() {
        setChanged(getLevel(), getBlockPos(), getBlockState());

        if(level == null || level.isClientSide) return;

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public int getRfTick() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().energy();
        }

        return (int) (ConfigLoader.getInstance().getMinerConfig(name).energyTick() * mod);
    }

    public int getMaxProgress() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().speed();
        }

        return (int) (ConfigLoader.getInstance().getMinerConfig(name).duration() * mod);
    }

    public ItemStack getBoostedStack(ItemStack base) {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().item();
        }

        int count = (int) (base.getCount() * mod);
        return base.copyWithCount(count);
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        for (int i = 0; i < 320; i++) {
            BlockPos check = pos.below(i);

            if(level.getBlockState(check).is(Blocks.BEDROCK)) return true;

            if (level.getBlockState(check).propagatesSkylightDown(level, check) || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;
            
            return false;
        }

        return true;
    }

    private boolean isItemHandlerFull() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getStackInSlot(i).getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private List<MinerRecipe> allRecipes() {
        if (level == null || level.isClientSide) {
            return new ArrayList<>();
        }

        if (structure == null) {
            return new ArrayList<>();
        }

        return level.getRecipeManager().getAllRecipesFor(MinerRecipe.Type.INSTANCE)
            .stream()
            .map(net.minecraft.world.item.crafting.RecipeHolder::value)
            .filter(recipe -> {
                if (recipe.allowHigherTiers()) {
                    return recipe.minTier() <= MiscUtil.tierMap.get(structure.getPath());
                } else {
                    return recipe.minTier() == MiscUtil.tierMap.get(structure.getPath());
                }
            })
            .filter(recipe -> recipe.dimension().equals(this.level.dimension()))
            .toList();
    }

    private boolean isItemValid(ItemStack stack, ItemStack handler) {
        return handler.isEmpty() || handler.is(stack.getItem()) && stack.getCount() + handler.getCount() <= handler.getMaxStackSize();
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);
    }

    public ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        float totalWeight = ListUtil.getTotalWeight(items);

        float randomValue = random.nextFloat() * totalWeight;

        for (WeightedStack item : items) {
            randomValue -= item.weight;
            if (randomValue <= 0) {
                return item.stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, Rotation.NONE);
        
        if (pattern == null) {
            foundStructure = false;
            return;
        }

        MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.NONE);
        
        if (result == null) {
            foundStructure = false;
            return;
        }
        
        if (!pattern.ID().equals(structure)) {
            foundStructure = false;
            return;
        }

        modifierMap.clear();
        foundStructure = true;
        result.blocks().stream()
            .filter(block -> block.getState().getBlock() instanceof ModifierBlock)
            .forEach(block -> {
                ConfigLoader.ModifierConfig modifier = ConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                if (!modifierMap.containsKey(block)) {
                    modifierMap.put(block, modifier);
                }
            });
    }

    public ResourceLocation getStructure() {
        return structure;
    }
}