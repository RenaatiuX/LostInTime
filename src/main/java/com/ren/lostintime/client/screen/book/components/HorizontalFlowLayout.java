package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.antlr.v4.runtime.misc.Triple;

import java.util.LinkedList;
import java.util.List;

public class HorizontalFlowLayout extends PageComponent {


    protected List<Triple<PageComponent, Inset, Dimension>> pageComponents = new LinkedList<>();

    public void addComponent(PageComponent component, Inset inset, Dimension dimension) {
        pageComponents.add(new Triple<>(component, inset, dimension));
    }

    public void addComponent(PageComponent component) {
        pageComponents.add(new Triple<>(component, Inset.ZERO, Dimension.fill()));
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        for (var componentTriple : this.pageComponents) {
            var component = componentTriple.a;
            if (component.getBounds().contains(mouseX, mouseY)) {
                return component.onClick(mouseX, mouseY, button);
            }
        }

        return super.onClick(mouseX, mouseY, button);
    }

    @Override
    public void updateBounds() {
        super.updateBounds();

        int currentY = this.y;
        int currentX = this.x;

        for (Triple<PageComponent, Inset, Dimension> pair : pageComponents) {
            PageComponent component = pair.a;
            Inset insets = pair.b;
            Dimension dimension = pair.c;

            var leftMargin = insets.resolveLeft(component.getBounds().width, this.width);
            currentX += leftMargin;
            var top = insets.resolveTop(component.getBounds().height, this.height);

            component.setX(currentX).setY(currentY + top);

            component.setWidth(dimension.resolveWidth(this.width, this.width - currentX + this.x));
            component.setHeight(dimension.resolveHeight(this.height, this.height) - top - insets.resolveBottom(component.getBounds().height, this.height));

            currentX += component.getBounds().width + insets.resolveRight(component.getBounds().width, this.width);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        pageComponents.forEach(t -> t.a.render(graphics, mouseX, mouseY, font, t.b, t.c, screen));
    }
}
