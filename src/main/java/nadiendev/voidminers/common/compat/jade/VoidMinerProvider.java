package nadiendev.voidminers.common.compat.jade;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.util.MiscUtil;
import nadiendev.voidminers.world.block.entity.ControllerBaseBE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum VoidMinerProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof ControllerBaseBE)) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();
        
        // Mostrar tier
        if (serverData.contains("Tier")) {
            int tier = serverData.getInt("Tier");
            tooltip.add(Component.translatable("jade.voidminers.tier", tier));
        }

        // Mostrar energía
        if (serverData.contains("Energy") && serverData.contains("MaxEnergy")) {
            int energy = serverData.getInt("Energy");
            int maxEnergy = serverData.getInt("MaxEnergy");
            tooltip.add(Component.translatable("jade.voidminers.energy", 
                String.format("%,d", energy), 
                String.format("%,d", maxEnergy)));
        }

        // Mostrar estado (trabajando/inactivo)
        if (serverData.contains("Working")) {
            boolean working = serverData.getBoolean("Working");
            Component status = working ? 
                Component.translatable("jade.voidminers.status.working") :
                Component.translatable("jade.voidminers.status.idle");
            tooltip.add(status);
        }

        // Mostrar progreso de trabajo
        if (serverData.contains("Progress") && serverData.contains("MaxProgress")) {
            int progress = serverData.getInt("Progress");
            int maxProgress = serverData.getInt("MaxProgress");
            
            if (maxProgress > 0) {
                int percentage = (int) ((progress / (float) maxProgress) * 100);
                tooltip.add(Component.translatable("jade.voidminers.progress", percentage));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof ControllerBaseBE miner)) {
            return;
        }

        // Obtener tier desde la estructura
        if (miner.getStructure() != null) {
            Integer tier = MiscUtil.tierMap.get(miner.getStructure().getPath());
            if (tier != null) {
                tag.putInt("Tier", tier);
            }
        }

        // Obtener energía
        tag.putInt("Energy", miner.getEnergyStorage().getEnergyStored());
        tag.putInt("MaxEnergy", miner.getEnergyStorage().getMaxEnergyStored());

        // Estado de trabajo
        tag.putBoolean("Working", miner.working);

        // Progreso de trabajo
        tag.putInt("Progress", miner.getProgress());
        tag.putInt("MaxProgress", miner.getMaxProgress());
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "void_miner");
    }
}