package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.common.entity.util.TimePeriod;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.data.PackOutput;;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class LITLanguageProvider extends LanguageProvider {

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

        // Time Periods
        add(TimePeriod.TRIASSIC, "Triassic");
        add(TimePeriod.JURASSIC, "Jurassic");
        add(TimePeriod.CRETACEOUS, "Cretaceous");
        add(TimePeriod.PALEOGENE, "Paleogene");
        add(TimePeriod.NEOGENE, "Neogene");
        add(TimePeriod.QUATERNARY, "Quaternary");

        // Descriptions
        add(TimePeriod.TRIASSIC.descriptionKey, "The Triassic Period (252-201 million years ago) marked the beginning of the Mesozoic Era. After a mass extinction, life slowly recovered, leading to the rise of reptiles and the very first dinosaurs.");
        add(TimePeriod.JURASSIC.descriptionKey, "The Jurassic Period (201-145 million years ago) was the golden age of dinosaurs. Giant sauropods roamed the land, while pterosaurs ruled the skies and large marine reptiles dominated the oceans.");
        add(TimePeriod.CRETACEOUS.descriptionKey, "The Cretaceous Period (145-66 million years ago) saw the diversification of dinosaurs, including iconic species like Tyrannosaurus Rex and Triceratops. It ended with a cataclysmic extinction event that wiped out the non-avian dinosaurs.");
        add(TimePeriod.PALEOGENE.descriptionKey, "The Paleogene Period (66-23 million years ago) began after the dinosaurs' extinction. With the dominant reptiles gone, mammals rapidly diversified, evolving from small, simple forms into a wide variety of new species.");
        add(TimePeriod.NEOGENE.descriptionKey, "The Neogene Period (23-2.6 million years ago) was a time of cooling climates. Grasslands expanded, and many modern mammals and birds continued to evolve, including the earliest human ancestors.");
        add(TimePeriod.QUATERNARY.descriptionKey, "The Quaternary Period (2.6 million years ago to today) is defined by dramatic ice ages and the rise of Homo sapiens. It encompasses all of modern human history, from early tool use to the development of civilization.");
    }

    private void add(TimePeriod period, String name) {
        add("lostintime.timeperiod." + period.name().toLowerCase(Locale.ROOT), name);
    }
}