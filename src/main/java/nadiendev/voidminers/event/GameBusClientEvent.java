package nadiendev.voidminers.event;

import nadiendev.voidminers.VoidMiners;
import nadiendev.voidminers.world.block.ModifierBlock;
import nadiendev.voidminers.config.ConfigLoader;
import nadiendev.voidminers.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = VoidMiners.MODID, value = Dist.CLIENT)
public class GameBusClientEvent {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent toolTipEvent) {
        List<Component> toolTip = toolTipEvent.getToolTip();
        ItemStack itemStack = toolTipEvent.getItemStack();

        if(itemStack.is(ModItems.STRUCTURE_HELPER.get())) {
            toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + "creative_only").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }

        if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        if (!(blockItem.getBlock() instanceof ModifierBlock mb)) {
            return;
        }

        ConfigLoader.ModifierConfig modConfig = ConfigLoader.getInstance().getModifierConfig(mb);

        toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".energy", modConfig.energy()).withStyle(ChatFormatting.DARK_RED));
        toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".speed", modConfig.speed()).withStyle(ChatFormatting.DARK_GREEN));
        toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".item", modConfig.item()).withStyle(ChatFormatting.DARK_BLUE));
    }
}