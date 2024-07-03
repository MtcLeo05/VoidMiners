package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ServerConfig;
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


        if (itemStack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof ModifierBlock mb) {
                Float[] values = ServerConfig.getModifiersFromTypeAndName(mb.name, mb.type);

                toolTip.add(
                    Component.translatable(
                        VoidMiners.MODID + ".tooltip.energy", values[0]
                    ).withStyle(ChatFormatting.DARK_RED)
                );

                toolTip.add(
                    Component.translatable(
                        VoidMiners.MODID + ".tooltip.speed", values[1]
                    ).withStyle(ChatFormatting.DARK_GREEN)
                );

                toolTip.add(
                    Component.translatable(
                        VoidMiners.MODID + ".tooltip.item", values[2]
                    ).withStyle(ChatFormatting.DARK_BLUE)
                );
            }
        }
    }

}
