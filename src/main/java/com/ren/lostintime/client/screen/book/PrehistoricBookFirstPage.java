package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PrehistoricBookFirstPage extends Page{

    protected ImageComponent imageComponent;
    protected TextPageComponent title;

    public PrehistoricBookFirstPage() {
        this.imageComponent = new ImageComponent(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/lost_in_time_icon.png"), 315, 205);
        this.title = new TextPageComponent(Component.translatable(PrehistoricBookScreen.TITLE_TRANSLATION_KEY));
    }

    @Override
    public void updateBounds() {
        super.updateBounds();
        title.setWidth(this.width - 10);
        ScreenRenderingUtils.centerVertically(this.getBounds(), title);
        imageComponent.setY(this.y + title.height);
        imageComponent.setX(this.x);
        ScreenRenderingUtils.center(this.getBounds(), imageComponent);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        title.render(graphics, mouseX, mouseY, font, screen);
        imageComponent.render(graphics, mouseX, mouseY, font, screen);
    }
}
