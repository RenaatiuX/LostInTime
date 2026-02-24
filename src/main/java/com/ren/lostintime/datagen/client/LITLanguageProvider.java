package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
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

        add("container.lostintime.soul_extractor", "Soul Extractor");
        add("container.lostintime.soul_configurator", "Soul Configurator");
        add("container.lostintime.transfigurator", "Transfigurator");
        add("container.lostintime.identification_table", "Identification Table");
        add("itemGroup.lostintime", "Lost In Time");
        add("lostintime.tooltip.residue", "Residue: %s / %s");
    }
}
