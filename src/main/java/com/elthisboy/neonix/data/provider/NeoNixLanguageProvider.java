package com.elthisboy.neonix.data.provider;

import com.elthisboy.neonix.NeoNix;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;
import com.elthisboy.neonix.init.*;


import java.util.concurrent.CompletableFuture;

public class NeoNixLanguageProvider extends FabricLanguageProvider {
    public NeoNixLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    private static void addText(@NotNull TranslationBuilder builder, @NotNull Text text, @NotNull String value){
        if(text.getContent() instanceof TranslatableTextContent translatableTextContent){
            builder.add(translatableTextContent.getKey(), value);
        }else{
            NeoNix.LOGGER.warn("Failded to add translation for text {}", text.getString());
        }
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {

        addText(translationBuilder, ItemGroupInit.NEO_NIX_TEXT, "Neo-Nix Mod");


        //.................................................TOOLS................................................
        translationBuilder.add(ItemInit.HOLO_PICKAXE,"Holo-Pickaxe");
        translationBuilder.add("text.neonix.overclock_on", "§bOVERCLOCK §factivated: §e%s §ffree blocks");
        translationBuilder.add("text.neonix.recharge", "Recharge +%s → Energy: %s/%s");
        translationBuilder.add("text.neonix.energy_max", "Energy is already full (%s).");
        translationBuilder.add("text.neonix.no_charge", "You don't have any HOLO CHARGE.");


        translationBuilder.add(ItemInit.HOLO_AXE,"Holo-Axe");

        translationBuilder.add(ItemInit.HOLO_HOE,"Holo-Hoe");


        translationBuilder.add(ItemInit.HOLO_SHOVEL,"Holo-Shovel");
        translationBuilder.add("message.neonix.holoshovel.full", "Energy is already full (%s).");
        translationBuilder.add("message.neonix.holoshovel.recharge", "Recharged +%s → Energy: %s/%s");
        translationBuilder.add("message.neonix.holoshovel.nocharge", "You have no HOLO CHARGE to recharge.");
        translationBuilder.add("message.neonix.holoshovel.overclock", "§bOVERCLOCK activated: §e%s §fblocks free!");

        translationBuilder.add(ItemInit.HOLO_SWORD,"Holo-Sword");
        translationBuilder.add("message.neonix.holosword.overclock", "§cOVERCLOCK activated: %s Hits free!");
        translationBuilder.add("message.neonix.holosword.full", "Energy is already full (%s).");
        translationBuilder.add("message.neonix.holosword.recharge", "Recharged +%s → Energy: %s/%s");
        translationBuilder.add("message.neonix.holosword.nocharge", "You have no HOLO CHARGE to recharge.");

        //.................................................ITEMS................................................
        translationBuilder.add(ItemInit.HOLO_CHARGE_LV1,"Holo-Charge lv1");
        translationBuilder.add(ItemInit.HOLO_CHARGE_LV2,"Holo-Charge lv2");
        translationBuilder.add(ItemInit.HOLO_CHARGE_LV3,"Holo-Charge lv3");


        translationBuilder.add("lore.neonix.charge", "Holo-Charge");
        translationBuilder.add("lore.neonix.charge.lv1", "+50 Energy");
        translationBuilder.add("lore.neonix.charge.lv2", "+125 Energy");
        translationBuilder.add("lore.neonix.charge.lv3", "+250 Energy");

        translationBuilder.add("lore.neonix.holo", "Holo Technology");
        translationBuilder.add("lore.neonix.energy_based", "Uses energy instead of durability.");

        translationBuilder.add("lore.neonix.sword_hint", "Hits harder during Overclock.");
        translationBuilder.add("lore.neonix.axe_hint", "Chops faster during Overclock.");
        translationBuilder.add("lore.neonix.shovel_hint", "Creates paths and digs; uses energy.");
        translationBuilder.add("lore.neonix.hoe_hint", "Tills soil; uses energy. Overclock boosts speed.");



    }
}
