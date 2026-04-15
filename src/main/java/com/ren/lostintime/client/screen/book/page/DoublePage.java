package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * just a placeholder to make the screen display double pages, does no actual calculation, this is done purely inside the pages themselfes, also setBound is directly forwarded to the left and right pages
 */
public class DoublePage extends Page {

    private final Page leftPage;
    private final Page rightPage;

    public DoublePage(Page leftPage, Page rightPage) {
        this.leftPage = leftPage;
        this.rightPage = rightPage;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        if (this.leftPage != null) {
            this.leftPage.render(graphics, mouseX, mouseY, font, screen);
        }
        if (this.rightPage != null) {
            this.rightPage.render(graphics, mouseX, mouseY, font, screen);
        }
    }

    public Page getLeftPage() {
        return leftPage;
    }

    public Page getRightPage() {
        return rightPage;
    }
}
