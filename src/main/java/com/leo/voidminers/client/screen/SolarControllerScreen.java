package com.leo.voidminers.client.screen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.entity.SolarControllerBE;
import com.leo.voidminers.menu.SolarControllerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;

public class SolarControllerScreen extends AbstractContainerScreen<SolarControllerMenu> {

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 140;

    // Trend state for production (FE/t) color feedback
    private int lastRfShown = Integer.MIN_VALUE;
    private int trendColor = 0xFF9E9E9E; // neutral default
    private long trendExpireMs = 0L;     // hold highlight for a short time

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
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTick, int mouseX, int mouseY) {
        // Render all custom UI at a higher Z to avoid being masked by slot rendering
        gg.pose().pushPose();
        gg.pose().translate(0, 0, 200f);

        // Center panel
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Accent color from BE
        int accent = getAccentColor();
        int accentDark = darken(accent, 0.65f);
        
        // State color (used for energy fill + stats)
        boolean active = menu.isActive();
        boolean working = menu.isWorking();
        boolean bufferFull = menu.isBufferFull();
        int statusColor = working ? 0xFF2ECC71 : (active && bufferFull ? 0xFFE74C3C : 0xFF9E9E9E);
        int bg = 0xCC101010; // translucent dark backdrop

        // Backdrop blur-ish overlay
        gg.fill(0, 0, this.width, this.height, 0x88000000);

        // Card base with border
        gg.fill(x, y, x + imageWidth, y + imageHeight, 0xEE141414);
        // Top gradient bar
        gg.fillGradient(x, y, x + imageWidth, y + 22, accent, accentDark, 0);

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
            int barC1 = lighten(statusColor, 0.20f);
            int barC2 = statusColor;
            gg.fillGradient(barX, barY, barX + fillW, barY + barH, barC1, barC2, 0);
        }
        String energyText = String.format("%,d / %,d FE", menu.energyStored(), menu.maxEnergy());
        int etw = this.font.width(energyText);
        int avail = barW - 8; // padding inside bar
        float scale = 1.0f;
        if (etw > avail) {
            scale = Math.max(0.6f, (float) avail / (float) etw);
        }
        int textX = x + (int) ((imageWidth - (etw * scale)) / 2f);
        int textY = barY + 4;
        gg.pose().pushPose();
        gg.pose().scale(scale, scale, 1f);
        gg.drawString(this.font, energyText, Math.round(textX / scale), Math.round(textY / scale), 0xFFEEEEEE, false);
        gg.pose().popPose();

        // RF/t row (display 0 when buffer is full) + Sun percent on right
        int rowY = barY + 22;
        int colW = (imageWidth / 2) - 16; // max width per column for values
        int rfShown = (!menu.isActive() || menu.isBufferFull()) ? 0 : menu.rfPerTick();

        // Compute trend-based color for production (green up, red down), with a short hold
        long now = System.currentTimeMillis();
        if (rfShown != lastRfShown) {
            if (lastRfShown != Integer.MIN_VALUE) {
                trendColor = (rfShown > lastRfShown) ? 0xFF2ECC71 : 0xFFE74C3C; // green up, red down
                trendExpireMs = now + 1000L; // keep highlight ~1s
            }
            lastRfShown = rfShown;
        }
        int prodColor = (now <= trendExpireMs) ? trendColor : statusColor;

        drawStat(gg, x + 12, rowY, Component.translatable("screen." + VoidMiners.MODID + ".solar.rft"), String.format("%,d", rfShown), prodColor, colW);
        int sun = menu.sunPercent();
        drawStat(gg, x + imageWidth / 2 + 4, rowY, Component.translatable("screen." + VoidMiners.MODID + ".solar.sun"), sun + "%", statusColor, colW);

        // Additional metrics: FE/s and FE/min
        int rfPerTick = rfShown;
        long fePerSec = Math.max(0L, (long) rfPerTick * 20L);
        long fePerMin = fePerSec * 60L;
        // Move FE/s and FE/min a bit lower for clarity
        int rowY2 = rowY + 26;
        drawStat(gg, x + 12, rowY2, Component.translatable("screen." + VoidMiners.MODID + ".solar.fes"), String.format("%,d", fePerSec), prodColor, colW);
        drawStat(gg, x + imageWidth / 2 + 4, rowY2, Component.translatable("screen." + VoidMiners.MODID + ".solar.femin"), String.format("%,d", fePerMin), prodColor, colW);

        // Status pill: consider buffer full as inactive (no generation)
        Component statusText = working
                ? Component.translatable("screen." + VoidMiners.MODID + ".solar.status.working")
                : Component.translatable("screen." + VoidMiners.MODID + ".solar.status.inactive");
        drawPill(gg, x + 12, y + imageHeight - 28, imageWidth - 24, 16, statusColor, 0x22222222);
        gg.drawCenteredString(this.font, statusText, x + imageWidth / 2, y + imageHeight - 24, 0xFF101010);

        gg.pose().popPose();
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gg);
        super.render(gg, mouseX, mouseY, partialTick);
        this.renderTooltip(gg, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        // Draw the screen title once in white, above vanilla elements
        gg.pose().pushPose();
        gg.pose().translate(0, 0, 210f);
        gg.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF, false);
        gg.pose().popPose();
        // Hide the vanilla inventory label via high coordinates (already set in ctor)
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawLabeledBar(GuiGraphics gg, int x, int y, Component label, int color) {
        gg.drawString(this.font, label, x, y, color, false);
    }

    private void drawStat(GuiGraphics gg, int x, int y, Component label, String value, int valueColor, int maxWidth) {
        gg.drawString(this.font, label, x, y, 0xFFB0B0B0, false);
        int vw = this.font.width(value);
        float scale = 1.0f;
        if (vw > maxWidth) {
            scale = Math.max(0.5f, (float) maxWidth / (float) vw);
        }
        gg.pose().pushPose();
        gg.pose().scale(scale, scale, 1f);
        gg.drawString(this.font, value, Math.round(x / scale), Math.round((y + 12) / scale), valueColor, false);
        gg.pose().popPose();
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

    // Chart helpers removed

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
