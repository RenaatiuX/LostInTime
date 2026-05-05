package com.ren.lostintime.client.util;

import com.mojang.blaze3d.vertex.*;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.components.PageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.page.Page;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.entity.util.TimePeriod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScreenRenderingUtils {


    public static final ResourceLocation TIME_PERIODS_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/time_period.png");

    public static void centerHorizontally(Rectangle centerBounds, PageComponent target) {
        target.setX(centerBounds.x + (centerBounds.width - target.getBounds().width) / 2);
    }

    public static void centerVertically(Rectangle centerBounds, PageComponent target) {
        target.setY(centerBounds.y + (centerBounds.height - target.getBounds().height) / 2);
    }

    public static void center(Rectangle centerBounds, PageComponent target) {
        centerHorizontally(centerBounds, target);
        centerVertically(centerBounds, target);
    }

    public static void scissor(GuiGraphics graphics, Rectangle rect){
        graphics.enableScissor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
    }


    public static Rectangle centered(int centerX, int centerY, int radius) {
        return centered(new Vector2i(centerX, centerY), radius);
    }

    public static Rectangle centered(Vector2i center, int radius) {
        return new Rectangle(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }

    public static void fill(GuiGraphics graphics, Rectangle rect, int color) {
        graphics.fill(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color);
    }

    public static Rectangle growRectangle(Rectangle rect, int w){
        return growRectangle(rect, w, w);
    }

    public static Rectangle growRectangle(Rectangle rect, int w, int h){
        return new Rectangle(rect.x - w, rect.y - h, rect.width + 2 * w, rect.height + 2 * h);
    }

    public static void renderTriangleWithOutline(PoseStack stack, Vector2i first, Vector2i second, Vector2i third, double margin, int foregroundColor, int backgroundColor) {
        var modelMatrix = stack.last().pose();

        // Background Color (Outline)
        float bgA = (float) FastColor.ARGB32.alpha(backgroundColor) / 255.0F;
        float bgR = (float) FastColor.ARGB32.red(backgroundColor) / 255.0F;
        float bgG = (float) FastColor.ARGB32.green(backgroundColor) / 255.0F;
        float bgB = (float) FastColor.ARGB32.blue(backgroundColor) / 255.0F;

        // Foreground Color (Inner)
        float fgA = (float) FastColor.ARGB32.alpha(foregroundColor) / 255.0F;
        float fgR = (float) FastColor.ARGB32.red(foregroundColor) / 255.0F;
        float fgG = (float) FastColor.ARGB32.green(foregroundColor) / 255.0F;
        float fgB = (float) FastColor.ARGB32.blue(foregroundColor) / 255.0F;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        // 1. Render Outer Triangle (Background)
        buffer.vertex(modelMatrix, first.x(), first.y(), 0.0F).color(bgR, bgG, bgB, bgA).endVertex();
        buffer.vertex(modelMatrix, second.x(), second.y(), 0.0F).color(bgR, bgG, bgB, bgA).endVertex();
        buffer.vertex(modelMatrix, third.x(), third.y(), 0.0F).color(bgR, bgG, bgB, bgA).endVertex();

        // 2. Calculate the lengths of the sides
        double a = Math.hypot(second.x() - third.x(), second.y() - third.y());
        double b = Math.hypot(first.x() - third.x(), first.y() - third.y());
        double c = Math.hypot(first.x() - second.x(), first.y() - second.y());

        double perimeter = a + b + c;

        // 3. Calculate Inner Triangle (only if it hasn't collapsed into itself)
        if (perimeter > 0) {
            double incenterX = (a * first.x() + b * second.x() + c * third.x()) / perimeter;
            double incenterY = (a * first.y() + b * second.y() + c * third.y()) / perimeter;

            double area = 0.5 * Math.abs(first.x() * (second.y() - third.y()) + second.x() * (third.y() - first.y()) + third.x() * (first.y() - second.y()));
            double inradius = (2 * area) / perimeter;

            if (margin < inradius) {
                double shrinkFactor = margin / inradius;

                // Shrink vertices exactly towards the incenter to achieve a uniform margin
                buffer.vertex(modelMatrix, (float) (first.x() + (incenterX - first.x()) * shrinkFactor), (float) (first.y() + (incenterY - first.y()) * shrinkFactor), 0.0F).color(fgR, fgG, fgB, fgA).endVertex();
                buffer.vertex(modelMatrix, (float) (second.x() + (incenterX - second.x()) * shrinkFactor), (float) (second.y() + (incenterY - second.y()) * shrinkFactor), 0.0F).color(fgR, fgG, fgB, fgA).endVertex();
                buffer.vertex(modelMatrix, (float) (third.x() + (incenterX - third.x()) * shrinkFactor), (float) (third.y() + (incenterY - third.y()) * shrinkFactor), 0.0F).color(fgR, fgG, fgB, fgA).endVertex();
            }
        }

        tessellator.end();
    }

    public static void renderTriangle(PoseStack stack, Vector2i first, Vector2i second, Vector2i third, int color) {

        var modelMatrix = stack.last().pose();

        float alpha = (float) FastColor.ARGB32.alpha(color) / 255.0F;
        float red = (float) FastColor.ARGB32.red(color) / 255.0F;
        float green = (float) FastColor.ARGB32.green(color) / 255.0F;
        float blue = (float) FastColor.ARGB32.blue(color) / 255.0F;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(modelMatrix, first.x(), first.y(), 0.0F).color(red, green, blue, alpha).endVertex();
        buffer.vertex(modelMatrix, second.x(), second.y(), 0.0F).color(red, green, blue, alpha).endVertex();
        buffer.vertex(modelMatrix, third.x(), third.y(), 0.0F).color(red, green, blue, alpha).endVertex();

        tessellator.end();

    }

    public static void renderCenteredTriangleIcon(GuiGraphics graphics, int x, int y, int maxWidth, int maxHeight) {
        final float iconWidth = 5.0f;
        final float iconHeight = 5.0f;

        var stack = graphics.pose();

        // 1. Calculate the scale factors for both axes
        float scaleX = maxWidth / iconWidth;
        float scaleY = maxHeight / iconHeight;

        // 2. Use the smaller scale factor to maintain aspect ratio and fit within the bounds
        float scale = Math.min(scaleX, scaleY);

        stack.pushPose();
        stack.translate(x - Math.round((scale * iconWidth) / 2f), y, 0);
        stack.scale(scale, scale, 1.0f);

        graphics.blit(TIME_PERIODS_TEXTURE, 0, 0, 0, 9, 5, 5, 128, 128);

        stack.popPose();
    }

    public static void renderTimePeriodTooltip(GuiGraphics graphics, int x, int y, TimePeriod period, boolean extended, boolean xCentered) {
        var title = Component.translatable(period.descriptionKey.replace(".desc", "")).plainCopy().withStyle(Style.EMPTY.withColor(period.color));
        var lines = new ArrayList<Component>();
        lines.add(title);

        if (extended) {
            lines.add(period.getEraDescription());
        }
        List<ClientTooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, lines, x, graphics.guiWidth() / 2, graphics.guiHeight(), Minecraft.getInstance().font);

        graphics.renderTooltipInternal(Minecraft.getInstance().font, components, x, y, (pScreenWidth, pScreenHeight, pMouseX, pMouseY, pTooltipWidth, pTooltipHeight) -> {
            return new Vector2i(pMouseX - pTooltipWidth / 2, pMouseY);
        });


    }

    public static void addTitleComponents(Page page, Component text) {
        addTitleComponents(page, text, 0x558B7D6B);
    }

    public static void addTitleComponents(Page page, Component text, int color) {
        addTitleComponents(page, text, color, color, true, false);
    }

    public static void addTitleComponents(Page page, Component text, int underlineColor, int textColor, boolean centered, boolean backDrop) {
        page.addComponent(new TitlePageComponent(text, textColor, backDrop, centered),
                Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.1f))
        );
        page.addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, underlineColor),
                Inset.symmetric(LayoutValue.px(2), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(1)));
    }
}
