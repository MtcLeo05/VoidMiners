package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.util.MapUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeBusClientEvent {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent toolTipEvent) {
        List<Component> toolTip = toolTipEvent.getToolTip();
        ItemStack itemStack = toolTipEvent.getItemStack();

        if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        if (!(blockItem.getBlock() instanceof ModifierBlock mb)) {
            return;
        }


        ConfigLoader.ModifierConfig modConfig = ConfigLoader.getInstance().getModifierConfig(mb.name, mb.type.type);

        toolTip.add(
            Component.translatable(
                VoidMiners.MODID + ".tooltip.energy", modConfig.energy()
            ).withStyle(ChatFormatting.DARK_RED)
        );

        toolTip.add(
            Component.translatable(
                VoidMiners.MODID + ".tooltip.speed", modConfig.speed()
            ).withStyle(ChatFormatting.DARK_GREEN)
        );

        toolTip.add(
            Component.translatable(
                VoidMiners.MODID + ".tooltip.item", modConfig.item()
            ).withStyle(ChatFormatting.DARK_BLUE)
        );
    }

}
