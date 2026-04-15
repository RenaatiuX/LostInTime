package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.PageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.antlr.v4.runtime.misc.Triple;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class Page {

    public int x;
    public int y;
    public int width;
    public int height;

    private boolean scissor = true;

    protected List<Triple<PageComponent, Inset, com.ren.lostintime.client.screen.book.util.Dimension>> pageComponents = new LinkedList<>();

    public void setBounds(Rectangle rect) {
        setBounds(rect.x, rect.y, rect.width, rect.height);
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateBounds();
    }

    public Page disableScisscor(){
        this.scissor = false;
        return this;
    }

    public void addComponent(PageComponent component, Inset inset, com.ren.lostintime.client.screen.book.util.Dimension dimension) {
        pageComponents.add(new Triple<>(component, inset, dimension));
    }

    public void addComponent(PageComponent component) {
        pageComponents.add(new Triple<>(component, Inset.ZERO, com.ren.lostintime.client.screen.book.util.Dimension.fill()));
    }

    public void updateBounds() {
        int currentY = this.y;

        for (Triple<PageComponent, Inset, com.ren.lostintime.client.screen.book.util.Dimension> pair : pageComponents) {
            PageComponent component = pair.a;
            Inset insets = pair.b;
            Dimension dimension = pair.c;

            var topMargin = insets.resolveTop(component.getBounds().height, this.height);
            currentY += topMargin;
            var left = insets.resolveLeft(component.getBounds().width, this.width);

            component.setX(this.x + left).setY(currentY);

            var calculatedWidth = dimension.resolveWidth(this.width, this.width);
            if (dimension.width().unit() == DimensionValue.Unit.NONE) {
                calculatedWidth = component.getBounds().width;
            }

            component.setWidth(calculatedWidth - left - insets.resolveRight(component.getBounds().width, this.width));

            var calculatedHeight = dimension.resolveHeight(this.height, this.height - this.y + currentY);
            if (dimension.height().unit() == DimensionValue.Unit.NONE) {
                calculatedHeight = component.getBounds().height;
            }

            var componentHeight = calculatedHeight - topMargin - insets.resolveBottom(component.getBounds().height, this.height);
            component.setHeight(componentHeight);

            currentY += componentHeight;

        }

    }

    public boolean isInside(int x, int y) {
        return getBounds().contains(x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean onClick(int mouseX, int mouseY, int button) {
        return false;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        //basically ensuring that only inside the bounds is drawn
        if (scissor)
            graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        for (var pair : pageComponents) {
            PageComponent component = pair.a;
            component.render(graphics, mouseX, mouseY, font, screen);
        }
        if (scissor)
            graphics.disableScissor();
    }
}
