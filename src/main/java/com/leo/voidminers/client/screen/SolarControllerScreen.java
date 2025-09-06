package com.leo.voidminers.client.screen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.entity.SolarControllerBE;
import com.leo.voidminers.menu.SolarControllerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;

public class SolarControllerScreen extends AbstractContainerScreen<SolarControllerMenu> {

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 140;

    public SolarControllerScreen(SolarControllerMenu menu, net.minecraft.world.entity.player.Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
        this.titleLabelX = 12;
        this.titleLabelY = 8;
        this.inventoryLabelX = 10000; // hide vanilla inventory label
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY) {
        // Center panel
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Accent color from BE
        int accent = getAccentColor();
        int accentDark = darken(accent, 0.65f);
        int bg = 0xCC101010; // translucent dark backdrop

        // Backdrop blur-ish overlay
        gg.fill(0, 0, this.width, this.height, 0x88000000);

        // Card base with border
        gg.fill(x, y, x + imageWidth, y + imageHeight, 0xEE141414);
        // Top gradient bar
        gg.fillGradient(x, y, x + imageWidth, y + 22, accent, accentDark, 0);

        // Title text
        gg.drawString(this.font, Component.translatable("screen." + VoidMiners.MODID + ".solar.title"), x + 10, y + 8, 0xFFFFFFFF, false);

        // Energy section
        int barX = x + 12;
        int barY = y + 40;
        int barW = imageWidth - 24;
        int barH = 14;
        drawLabeledBar(gg, barX, barY - 12, Component.translatable("screen." + VoidMiners.MODID + ".solar.energy"), 0xFFB0B0B0);
        // Bar background
        gg.fill(barX, barY, barX + barW, barY + barH, 0xFF202020);
        float ratio = Math.min(1f, (float) menu.energyStored() / (float) menu.maxEnergy());
        int fillW = (int) (barW * ratio);
        if (fillW > 0) {
            gg.fillGradient(barX, barY, barX + fillW, barY + barH, accent, accentDark, 0);
        }
        String energyText = String.format("%,d / %,d FE", menu.energyStored(), menu.maxEnergy());
        int etw = this.font.width(energyText);
        gg.drawString(this.font, energyText, x + (imageWidth - etw) / 2, barY + 4, 0xFFEEEEEE, false);

        // RF/t and Sun row
        int rowY = barY + 28;
        drawStat(gg, x + 12, rowY, Component.translatable("screen." + VoidMiners.MODID + ".solar.rft"), String.format("%,d", menu.rfPerTick()), accent);
        drawStat(gg, x + imageWidth / 2 + 4, rowY, Component.translatable("screen." + VoidMiners.MODID + ".solar.sun"), menu.sunPercent() + "%", accent);

        // Status pill
        boolean active = menu.isActive();
        boolean working = menu.isWorking();
        Component statusText;
        int statusColor;
        if (working) {
            statusText = Component.translatable("screen." + VoidMiners.MODID + ".solar.status.working");
            statusColor = 0xFF2ECC71; // green
        } else if (active) {
            statusText = Component.translatable("screen." + VoidMiners.MODID + ".solar.status.active");
            statusColor = 0xFFFFC107; // amber
        } else {
            statusText = Component.translatable("screen." + VoidMiners.MODID + ".solar.status.inactive");
            statusColor = 0xFFE74C3C; // red
        }
        drawPill(gg, x + 12, y + imageHeight - 28, imageWidth - 24, 16, statusColor, 0x22222222);
        gg.drawCenteredString(this.font, statusText, x + imageWidth / 2, y + imageHeight - 24, 0xFF101010);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gg);
        super.render(gg, mouseX, mouseY, partialTick);
        this.renderTooltip(gg, mouseX, mouseY);
    }

    private void drawLabeledBar(GuiGraphics gg, int x, int y, Component label, int color) {
        gg.drawString(this.font, label, x, y, color, false);
    }

    private void drawStat(GuiGraphics gg, int x, int y, Component label, String value, int accent) {
        gg.drawString(this.font, label, x, y, 0xFFB0B0B0, false);
        gg.drawString(this.font, value, x, y + 12, 0xFFFFFFFF, false);
    }

    private void drawPill(GuiGraphics gg, int x, int y, int w, int h, int color, int outline) {
        gg.fill(x, y, x + w, y + h, outline);
        gg.fillGradient(x + 1, y + 1, x + w - 1, y + h - 1, lighten(color, 0.15f), color, 0);
    }

    private int getAccentColor() {
        BlockEntity be = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(menu.getPos()) : null;
        if (be instanceof SolarControllerBE sc) {
            int c = sc.getBeamColor();
            // Ensure opaque ARGB
            return (0xFF000000) | c;
        }
        return 0xFF3FA9F5; // default blue
    }

    private static int darken(int argb, float amount) {
        Color c = new Color(argb, true);
        int r = Math.max(0, Math.round(c.getRed() * amount));
        int g = Math.max(0, Math.round(c.getGreen() * amount));
        int b = Math.max(0, Math.round(c.getBlue() * amount));
        return (c.getAlpha() << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lighten(int argb, float amount) {
        Color c = new Color(argb, true);
        int r = Math.min(255, Math.round(c.getRed() + (255 - c.getRed()) * amount));
        int g = Math.min(255, Math.round(c.getGreen() + (255 - c.getGreen()) * amount));
        int b = Math.min(255, Math.round(c.getBlue() + (255 - c.getBlue()) * amount));
        return (c.getAlpha() << 24) | (r << 16) | (g << 8) | b;
    }
}

