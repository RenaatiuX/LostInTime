package com.ren.lostintime.client.util;

import com.mojang.blaze3d.vertex.*;
import com.ren.lostintime.client.screen.book.PageComponent;
import net.minecraft.util.FastColor;
import org.joml.Vector2i;

import java.awt.*;

public class ScreenRenderingUtils {

    public static void centerHorizontally(Rectangle centerBounds, PageComponent target){
        target.setX(centerBounds.x + (centerBounds.width - target.getBounds().width) / 2);
    }
    public static void centerVertically(Rectangle centerBounds, PageComponent target){
        target.setY(centerBounds.y + (centerBounds.height - target.getBounds().height) / 2);
    }

    public static void center(Rectangle centerBounds, PageComponent target){
        centerHorizontally(centerBounds, target);
        centerVertically(centerBounds, target);
    }

    public static void renderTriangleWithOutline(PoseStack stack, Vector2i first, Vector2i second, Vector2i third,double margin, int foregroundColor, int backgroundColor){
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

    public static void renderTriangle(PoseStack stack, Vector2i first, Vector2i second, Vector2i third, int color){

        var modelMatrix = stack.last().pose();

        float alpha = (float) FastColor.ARGB32.alpha(color) / 255.0F;
        float red = (float)FastColor.ARGB32.red(color) / 255.0F;
        float green = (float)FastColor.ARGB32.green(color) / 255.0F;
        float blue = (float)FastColor.ARGB32.blue(color) / 255.0F;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(modelMatrix, first.x(), first.y(), 0.0F).color(red, green, blue, alpha).endVertex();
        buffer.vertex(modelMatrix, second.x(), second.y(), 0.0F).color(red, green, blue, alpha).endVertex();
        buffer.vertex(modelMatrix, third.x(), third.y(), 0.0F).color(red, green, blue, alpha).endVertex();

        tessellator.end();

    }
}
