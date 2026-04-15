package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.components.ImageComponent;
import com.ren.lostintime.client.screen.book.components.TextPageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PrehistoricBookFirstPage extends Page {

    public PrehistoricBookFirstPage() {
        //this.addComponent(new TextPageComponent(Component.translatable(PrehistoricBookScreen.TITLE_TRANSLATION_KEY), 0x000000, false, true), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)), new Dimension(DimensionValue.fill(), DimensionValue.none()));
        this.addComponent(
                new ImageComponent(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/logo.png"), 461, 461, true),
                Inset.ZERO,
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.8f))
        );
        this.addComponent(
                new TextPageComponent(Component.literal("Field Journal"), 0x403020, true),
                Inset.of(LayoutValue.px(0), LayoutValue.px(0), LayoutValue.px(0), LayoutValue.px(0)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.2f))
        );
        this.addComponent(
                new TextPageComponent(Component.literal("Restoring what was lost"), 0x605040, true),
                Inset.of(LayoutValue.px(5), LayoutValue.px(0), LayoutValue.px(0), LayoutValue.px(0)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.2f))
        );
    }
}
