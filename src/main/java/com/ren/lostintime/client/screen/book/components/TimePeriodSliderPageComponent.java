package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.page.PrehistoricBookScreen;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import com.ren.lostintime.common.entity.util.TimePeriod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.awt.*;

public class TimePeriodSliderPageComponent extends PageComponent {

    private TimePeriod period;
    private double minTime, maxTime;
    private final int backgroundColor, outlineColor;


    protected double sliderHeightPercentage = 0.4;


    public TimePeriodSliderPageComponent(TimePeriod period) {
        this(period, 0x55000000, 0xFF000000);

    }


    public TimePeriodSliderPageComponent(TimePeriod period, int backgroundColor, int outlineColor) {
        this.period = period;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        minTime = TimePeriod.getMinTime();
        maxTime = TimePeriod.getMaxTime();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        var bounds = this.getBounds();
        //graphics.enableScissor(bounds.x, bounds.y,(int) bounds.getMaxX(), (int)bounds.getMaxY());

        int outline = 0;

        double bottomLinePercentage = 0.05;

        double timeRange = maxTime - minTime;

        int sliderHeight = (int) (this.height * sliderHeightPercentage);
        int periodsHeight = this.height - sliderHeight;
        graphics.fill(bounds.x, bounds.y + sliderHeight, bounds.x + bounds.width, bounds.y + bounds.height, outlineColor);

        for (TimePeriod t : TimePeriod.values()) {
            double fromPercentage = 1 - (t.fromMa == 0 ? 0d : t.fromMa / timeRange);
            double toPercentage = 1 - (t.toMa == 0 ? 0d : t.toMa / timeRange);

            int fromWidth = Mth.floor((this.width - 2 * outline) * fromPercentage);
            int toWidth = Mth.floor((this.width - 2 * outline) * toPercentage);

            int color = t.color;
            if ((color >>> 24) == 0) {
                color |= 0xFF000000; // Force alpha to 255 (0xFF)
            }
            var colorObject = new Color(color);
            if (t != period) {
                colorObject = colorObject.darker().darker();
            }
            color = colorObject.getRGB();

            var timeBounds = new Rectangle(bounds.x + Math.min(toWidth, fromWidth) + outline, bounds.y + sliderHeight + outline, Math.abs(fromWidth - toWidth) + outline, bounds.height - outline);

            graphics.fill(bounds.x + Math.min(toWidth, fromWidth) + outline, bounds.y + sliderHeight + outline, bounds.x + Math.max(toWidth, fromWidth) + outline, bounds.y + bounds.height - outline, color);
            if (timeBounds.contains(mouseX, mouseY)) {
                ScreenRenderingUtils.renderTimePeriodTooltip(graphics, (int) timeBounds.getCenterX(), (int) bounds.getMaxY() + 5, t, t == this.period, false);
            }
        }

        graphics.fill(bounds.x + outline, bounds.y + sliderHeight + outline + (int) ((periodsHeight - outline) * (1 - bottomLinePercentage)), bounds.x + bounds.width - outline, bounds.y + bounds.height - outline, backgroundColor);

        //graphics.disableScissor();
        if (this.getBounds().contains(mouseX, mouseY)) {
            renderTriangle(graphics, mouseX);
        } else {
            double fromPercentage = 1d - (period.fromMa == 0 ? 0d : period.fromMa / timeRange);
            double toPercentage = 1 - (period.toMa == 0 ? 0d : period.toMa / timeRange);

            int fromWidth = Mth.ceil(this.width * fromPercentage);
            int toWidth = Mth.ceil(this.width * toPercentage);

            int middle = (fromWidth + toWidth) / 2;

            renderTriangle(graphics, this.x + middle);
        }


    }


    protected void renderTriangle(GuiGraphics graphics, int x) {
        int triangleRadius = 4;
        int sliderHeight = (int) (this.height * sliderHeightPercentage);

        ScreenRenderingUtils.renderCenteredTriangleIcon(graphics, x, this.getBounds().y, 5, sliderHeight);

                /*
        var firstPoint = new Vector2i(x - triangleRadius, this.y);
        var secondPoint = new Vector2i(x, this.y + sliderHeight);
        var thirdPoint = new Vector2i(x + triangleRadius, this.y);

        ScreenRenderingUtils.renderTriangleWithOutline(graphics.pose(), firstPoint, secondPoint, thirdPoint,0.4,0xFFFFFFFF, 0xFF555555);
        */

    }
}
