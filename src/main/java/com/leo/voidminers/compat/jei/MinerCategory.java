package com.leo.voidminers.compat.jei;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.recipe.JeiRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class MinerCategory implements IRecipeCategory<JeiRecipe> {
    public final ResourceLocation UID;
    public static final ResourceLocation TEXTURE = new ResourceLocation(VoidMiners.MODID, "textures/gui/jei_background.png");

    public RecipeType<JeiRecipe> RECIPE_TYPE;

    private final IDrawable background;
    private final IDrawable icon;
    public final Block blockIcon;
    public final int tier;

    public MinerCategory(IGuiHelper guiHelper, Block blockIcon, int tier) {
        UID = new ResourceLocation(VoidMiners.MODID, "miner/tier" + tier + "_miner");
        RECIPE_TYPE = new RecipeType<>(UID, JeiRecipe.class);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 125, 35);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, blockIcon.asItem().getDefaultInstance());
        this.blockIcon = blockIcon;
        this.tier = tier;
    }

    @Override
    public RecipeType<JeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("voidminers.gui.miner", tier);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JeiRecipe minerRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            4,
            4
        ).addItemStack(minerRecipe.getOutput().stack);
    }

    @Override
    public void draw(JeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        float per = 100 * (recipe.getOutput().weight / recipe.getTotalWeight());

        Component weight = Component.translatable(VoidMiners.MODID + ".structure.chance", customFormat(per) + "%");
        String dimensionName = recipe.getDimension().location().toString();

        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, weight, 24, 8, 0xFFFFFFFF);
        guiGraphics.drawString(font, dimensionName, 4, 21, 0xFFFFFFFF);
    }

    public static String customFormat(float number) {
        String numberStr = Float.toString(number);
        String[] parts = numberStr.split("\\.");

        String decimalPart = parts.length > 1 ? parts[1] : "";

        int lastNumber = 0;
        int checkNumber = 0;
        boolean checkNext = true;
        int zeroCount = 0;

        for (int i = 0; i < decimalPart.length(); i++) {
            if (decimalPart.charAt(i) != '0') {
                int currentCheck = Integer.parseInt(String.valueOf(decimalPart.charAt(i)));
                if (checkNext) {
                    lastNumber = currentCheck;
                    checkNext = false;
                } else {
                    checkNumber = currentCheck;
                    break;
                }
            } else {
                zeroCount++;
            }
        }

        if (checkNumber >= 6) {
            lastNumber++;
        }

        return parts[0] + "." + "0".repeat(Math.max(0, zeroCount)) + lastNumber;
    }
}
