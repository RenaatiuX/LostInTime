package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.LostInTime;
import net.minecraft.resources.ResourceLocation;

public class PrehistoricBookFirstPage extends Page{


    public PrehistoricBookFirstPage() {
        //this.addComponent(new TextPageComponent(Component.translatable(PrehistoricBookScreen.TITLE_TRANSLATION_KEY), 0x000000, false, true), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)), new Dimension(DimensionValue.fill(), DimensionValue.none()));
        this.addComponent(new ImageComponent(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/logo.png"), 461, 461).additionalScale(0.7f), Inset.ZERO, Dimension.fill());

    }
}
