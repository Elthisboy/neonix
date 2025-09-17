package com.elthisboy.neonix.data.provider;

import com.elthisboy.neonix.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class NeoNixItemTagProvider extends FabricTagProvider<Item> {
    public NeoNixItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    // Tags vanilla de 1.21.1 para encantamientos
    public static final TagKey<Item> ENCH_MINING =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "enchantable/mining"));
    public static final TagKey<Item> ENCH_DURABLE =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "enchantable/durable"));
    public static final TagKey<Item> ENCH_VANISHING =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "enchantable/vanishing_curse"));
    public static final TagKey<Item> ENCH_WEAPON  =
            TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "enchantable/weapon"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ENCH_MINING).add(ItemInit.HOLO_PICKAXE);
        getOrCreateTagBuilder(ENCH_DURABLE).add(ItemInit.HOLO_PICKAXE);

        getOrCreateTagBuilder(ENCH_MINING).add(ItemInit.HOLO_AXE);
        getOrCreateTagBuilder(ENCH_DURABLE).add(ItemInit.HOLO_AXE);

        getOrCreateTagBuilder(ENCH_MINING).add(ItemInit.HOLO_SHOVEL);
        getOrCreateTagBuilder(ENCH_DURABLE).add(ItemInit.HOLO_SHOVEL);

        getOrCreateTagBuilder(ENCH_MINING).add(ItemInit.HOLO_HOE);
        getOrCreateTagBuilder(ENCH_DURABLE).add(ItemInit.HOLO_HOE);

        getOrCreateTagBuilder(ENCH_WEAPON).add(ItemInit.HOLO_SWORD);
        getOrCreateTagBuilder(ENCH_DURABLE).add(ItemInit.HOLO_SWORD);

    }
}
