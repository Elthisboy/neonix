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
        super(dataOutput, registryLookup);
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


        //.................................................ITEMS................................................
        translationBuilder.add(ItemInit.HOLO_PICK,"Holo-Pick");

    }
}
