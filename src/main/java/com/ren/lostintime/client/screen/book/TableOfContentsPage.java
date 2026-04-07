package com.ren.lostintime.client.screen.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TableOfContentsPage extends Page {

    public TableOfContentsPage() {
        this.addComponent(new TitlePageComponent(Component.literal("Table of Contents")).maxScale(1.5f)
                        .addUnderline(2, 0x558B7D6B, Inset.symmetric(LayoutValue.px(3),
                                LayoutValue.px(7))), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new TitlePageComponent(Component.literal("I. The Geological Eras"), 0x302010, false, false),
                Inset.of(LayoutValue.px(5), LayoutValue.px(0), LayoutValue.px(5), LayoutValue.px(0)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        /*this.addComponent(
                new TextPageComponent(Component.literal("Records of the Paleozoic, Mesozoic, and Cenozoic worlds."), 0x605040, false),
                Inset.of(LayoutValue.px(2), LayoutValue.ZERO, LayoutValue.px(20), LayoutValue.px(10)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new TitlePageComponent(Component.literal("II. The Field Guide"), 0x302010, false, false).maxScale(1.1f),
                Inset.of(LayoutValue.px(20), LayoutValue.ZERO, LayoutValue.px(10), LayoutValue.ZERO),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new TextPageComponent(Component.literal("Detailed logs categorized by Fauna, Flora, and Anomalies."), 0x605040, false),
                Inset.of(LayoutValue.px(2), LayoutValue.ZERO, LayoutValue.px(20), LayoutValue.px(10)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new ImageComponent(ResourceLocation.withDefaultNamespace("textures/item/bone.png"), 16, 16, true).additionalScale(1.2f),
                Inset.of(LayoutValue.px(15), LayoutValue.ZERO, LayoutValue.ZERO, LayoutValue.ZERO),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(20))
        );*/

    }

}
