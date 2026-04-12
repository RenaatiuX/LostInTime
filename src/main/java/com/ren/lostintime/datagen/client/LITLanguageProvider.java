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
        add(TimePeriod.CAMBRIAN.descriptionKey.replace(".desc", ""), "Cambrian");
        add(TimePeriod.ORDOVICIAN.descriptionKey.replace(".desc", ""), "Ordovician");
        add(TimePeriod.SILURIAN.descriptionKey.replace(".desc", ""), "Silurian");
        add(TimePeriod.DEVONIAN.descriptionKey.replace(".desc", ""), "Devonian");
        add(TimePeriod.CARBONIFEROUS.descriptionKey.replace(".desc", ""), "Carboniferous");
        add(TimePeriod.PERMIAN.descriptionKey.replace(".desc", ""), "Permian");
        add(TimePeriod.TRIASSIC.descriptionKey.replace(".desc", ""), "Triassic");
        add(TimePeriod.JURASSIC.descriptionKey.replace(".desc", ""), "Jurassic");
        add(TimePeriod.CRETACEOUS.descriptionKey.replace(".desc", ""), "Cretaceous");
        add(TimePeriod.PALEOGENE.descriptionKey.replace(".desc", ""), "Paleogene");
        add(TimePeriod.NEOGENE.descriptionKey.replace(".desc", ""), "Neogene");
        add(TimePeriod.QUATERNARY.descriptionKey.replace(".desc", ""), "Quaternary");
        //add(TimePeriod.PHANEROZOIC.descriptionKey.replace(".desc", ""), "Phanerozoic Eon");

        // Descriptions
        add(TimePeriod.CAMBRIAN.descriptionKey, "The Cambrian (541-485 Ma) exploded with life. It saw the rise of arthropods like trilobites and the first complex marine ecosystems in history.");
        add(TimePeriod.ORDOVICIAN.descriptionKey, "During the Ordovician (485-443 Ma), the seas were filled with giant nautiloids and early jawless fish, while the first plants began to touch the barren land.");
        add(TimePeriod.SILURIAN.descriptionKey, "The Silurian (443-419 Ma) stabilized life after a great extinction. Jawed fish appeared, and terrestrial plants became more common near the water's edge.");
        add(TimePeriod.DEVONIAN.descriptionKey, "Known as the 'Age of Fishes', the Devonian (419-358 Ma) saw fish mastering the oceans and the first brave tetrapods starting to walk on land.");
        add(TimePeriod.CARBONIFEROUS.descriptionKey, "The Carboniferous (358-298 Ma) was a world of colossal swamps and giant insects. High oxygen levels allowed dragonflies the size of eagles to rule the air.");
        add(TimePeriod.PERMIAN.descriptionKey, "The Permian (298-252 Ma) saw the rise of synapsids, ancestors of mammals. It ended with the 'Great Dying', the most severe mass extinction ever known.");
        add(TimePeriod.TRIASSIC.descriptionKey, "The Triassic (252-201 Ma) marked the recovery of life. It saw the rise of the first dinosaurs and the split of the supercontinent Pangea.");
        add(TimePeriod.JURASSIC.descriptionKey, "The Jurassic (201-145 Ma) was the golden age of giants. Sauropods dominated the land while pterosaurs and marine reptiles ruled the skies and seas.");
        add(TimePeriod.CRETACEOUS.descriptionKey, "The Cretaceous (145-66 Ma) saw the peak of dinosaur diversity and the rise of flowering plants, ending with the famous asteroid impact.");
        add(TimePeriod.PALEOGENE.descriptionKey, "The Paleogene (66-23 Ma) followed the dinosaurs' demise. Mammals and birds rapidly diversified to fill the empty ecological niches.");
        add(TimePeriod.NEOGENE.descriptionKey, "During the Neogene (23-2.6 Ma), climates cooled and grasslands expanded. Many modern mammal groups, including early hominids, appeared.");
        add(TimePeriod.QUATERNARY.descriptionKey, "The Quaternary (2.6 Ma - Present) is the age of ice and humans. It is defined by repeated glaciations and the rise of modern civilization.");
        //add(TimePeriod.PHANEROZOIC.descriptionKey, "The Phanerozoic Eon encompasses the last 541 million years. It is the age of 'visible life', where complex organisms have flourished across the globe.");
    }

    private void add(TimePeriod period, String name) {
        add("lostintime.timeperiod." + period.name().toLowerCase(Locale.ROOT), name);
    }
}