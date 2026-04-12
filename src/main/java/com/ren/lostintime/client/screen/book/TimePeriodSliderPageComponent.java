package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import com.ren.lostintime.common.entity.util.TimePeriod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

public class TimePeriodSliderPageComponent extends PageComponent{

    private TimePeriod period;
    private double minTime, maxTime;
    private final int backgroundColor, timeColor, outlineColor;


    protected double sliderHeightPercentage = 0.4;

    public TimePeriodSliderPageComponent(TimePeriod period) {
        this(period, 0xFF0000FF);

    }

    public TimePeriodSliderPageComponent(TimePeriod period, int timeColor) {
        this(period, 0x558B7D6B, timeColor, 0xFF000000);

    }


    public TimePeriodSliderPageComponent(TimePeriod period, int backgroundColor, int timeColor, int outlineColor) {
        this.period = period;
        this.backgroundColor = backgroundColor;
        this.timeColor = timeColor;
        this.outlineColor = outlineColor;
        minTime = TimePeriod.getMinTime();
        maxTime = TimePeriod.getMaxTime();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        var bounds = this.getBounds();
        graphics.enableScissor(bounds.x, bounds.y,(int) bounds.getMaxX(), (int)bounds.getMaxY());

        int outline = 1;

        double bottomLinePercentage = 0.05;

        double timeRange = maxTime - minTime;

        int sliderHeight = (int) (this.height * sliderHeightPercentage);
        int periodsHeight = this.height - sliderHeight;
        graphics.fill(bounds.x, bounds.y + sliderHeight, bounds.x + bounds.width, bounds.y + bounds.height, outlineColor);

        for (TimePeriod t : TimePeriod.values()){
            double fromPercentage = t.fromMa == 0 ? 0d : t.fromMa / timeRange;
            double toPercentage = t.toMa == 0 ? 0d : t.toMa / timeRange;

            int fromWidth = Mth.ceil((this.width - 2 * outline) * fromPercentage);
            int toWidth = Mth.ceil((this.width - 2 * outline) * toPercentage);
            
            int color = t == this.period ? timeColor : t.color;
            if ((color >>> 24) == 0) {
                color |= 0xFF000000; // Force alpha to 255 (0xFF)
            }

            graphics.fill(bounds.x + toWidth + outline, bounds.y + sliderHeight + outline, bounds.x + fromWidth + outline, bounds.y + bounds.height - outline, color);
        }


        graphics.fill(bounds.x + outline, bounds.y + sliderHeight + outline + (int) ((periodsHeight - outline) * (1 - bottomLinePercentage)), bounds.x + bounds.width - outline, bounds.y + bounds.height - outline, backgroundColor);

        graphics.disableScissor();
        if (this.getBounds().contains(mouseX, mouseY)){
            renderTriangle(graphics, mouseX);
        }else {
            double fromPercentage = period.fromMa == 0 ? 0d : period.fromMa / timeRange;
            double toPercentage = period.toMa == 0 ? 0d : period.toMa / timeRange;

            int fromWidth = Mth.ceil(this.width * fromPercentage);
            int toWidth = Mth.ceil(this.width * toPercentage);

            int middle = (fromWidth + toWidth) / 2;

            renderTriangle(graphics, this.x + middle);
        }


    }


    protected void renderTriangle(GuiGraphics graphics, int x){
        int triangleRadius = 4;
        int sliderHeight = (int) (this.height * sliderHeightPercentage);

        var firstPoint = new Vector2i(x - triangleRadius, this.y);
        var secondPoint = new Vector2i(x, this.y + sliderHeight);
        var thirdPoint = new Vector2i(x + triangleRadius, this.y);

        ScreenRenderingUtils.renderTriangleWithOutline(graphics.pose(), firstPoint, secondPoint, thirdPoint,0.4,0xFFFFFFFF, 0xFF555555);
    }
}
