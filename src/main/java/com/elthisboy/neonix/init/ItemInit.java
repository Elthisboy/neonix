package com.elthisboy.neonix.init;

import com.elthisboy.neonix.NeoNix;
import com.elthisboy.neonix.customitem.HoloPickaxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import com.elthisboy.neonix.list.enums.NeoHoloNixModToolMaterials;


public class ItemInit {

    public static final Item EXAMPLE_ITEM = register("example_item", new Item(new Item.Settings()));

   //public static final SwordItem EXAMPLE_SWORD = register("example_sword",
   //        new SwordItem(NeoHoloNixModToolMaterials.EXAMPLE, new Item.Settings()
   //                .attributeModifiers(SwordItem.createAttributeModifiers(NeoHoloNixModToolMaterials.EXAMPLE, 3, -2.4f))));


    public static final PickaxeItem HOLO_PICK = register("holo_pick",
                new HoloPickaxeItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));


    public static <T extends Item> T register(String name, T item){
        return Registry.register(Registries.ITEM, NeoNix.id(name),item);
    }

    public static void load(){

    }

}
