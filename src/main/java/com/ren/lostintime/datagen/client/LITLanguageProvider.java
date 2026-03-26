package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.data.PackOutput;
import net.minecraftforge.registries.RegistryObject;

public class LITLanguageProvider extends LanguageProvider{

    public LITLanguageProvider(PackOutput output) {
        super(output, LostInTime.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ItemInit.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(this::simpleItem);

        add(PrehistoricBookScreen.TITLE_TRANSLATION_KEY, "Prehistoric Book");

        add("container.lostintime.soul_extractor", "Soul Extractor");
        add("container.lostintime.soul_configurator", "Soul Configurator");
        add("container.lostintime.transfigurator", "Transfigurator");
        add("container.lostintime.identification_table", "Identification Table");
        add("creative_tab.lostintime.lost_in_time", "Lost In Time");
        add("lostintime.tooltip.residue", "Residue: %s / %s");

        add("death.attack.bleeding", "%1$s bled to death");
        add("death.attack.bleeding.player", "%1$s bled to death while trying to escape %2$s");

        add("message.lostintime.infection_block", "The infection prevents your wounds from healing!");

        add("advancement.lostintime.tis_but_a_scratch.title", "'Tis But a Scratch!");
        add("advancement.lostintime.tis_but_a_scratch.desc", "Survive the bite of a prehistoric predator.");
    }
}
