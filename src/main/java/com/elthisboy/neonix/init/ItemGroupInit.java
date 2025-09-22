package com.elthisboy.neonix.init;

import com.elthisboy.neonix.NeoNix;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroupInit {

    public static final Text NEO_NIX_TEXT = Text.translatable("itemGroup."+ NeoNix.MOD_ID+ ".titleGroup");

    public static final ItemGroup NEO_NIX_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(NeoNix.MOD_ID, "neo_nix_group"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ItemInit.HOLO_PICKAXE)) // icono de la pestaña
                    .displayName(NEO_NIX_TEXT)
                    .entries((context, entries) -> {
                        // === Agregamos todos los ítems aquí ===
                        entries.add(ItemInit.HOLO_CHARGE_LV1);
                        entries.add(ItemInit.HOLO_CHARGE_LV2);
                        entries.add(ItemInit.HOLO_CHARGE_LV3);

                        entries.add(ItemInit.HOLO_PICKAXE);
                        entries.add(ItemInit.HOLO_SWORD);
                        entries.add(ItemInit.HOLO_AXE);
                        entries.add(ItemInit.HOLO_SHOVEL);
                        entries.add(ItemInit.HOLO_HOE);
                    })
                    .build()
    );
    public static void load(){}

}
