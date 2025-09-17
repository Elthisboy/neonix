package com.elthisboy.neonix.init;

import com.elthisboy.neonix.NeoNix;
import com.elthisboy.neonix.holoaxe.HoloAxeItem;
import com.elthisboy.neonix.holohoe.HoloHoeItem;
import com.elthisboy.neonix.holopickaxe.HoloPickaxeItem;
import com.elthisboy.neonix.holoshovel.HoloShovelItem;
import com.elthisboy.neonix.holosword.HoloSwordItem;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ItemInit {

    public static final Item HOLO_CHARGE_LV1 = register("holo_charge_lv1", new Item(new Item.Settings()));

    public static final Item HOLO_CHARGE_LV2 = register("holo_charge_lv2", new Item(new Item.Settings()));

    public static final Item HOLO_CHARGE_LV3 = register("holo_charge_lv3", new Item(new Item.Settings()));


    public static final PickaxeItem HOLO_PICKAXE = register("holo_pickaxe",
                new HoloPickaxeItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));

    public static final SwordItem HOLO_SWORD = register("holo_sword",
            new HoloSwordItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));






    public static final AxeItem HOLO_AXE = register("holo_axe",
            new HoloAxeItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));

    public static final ShovelItem HOLO_SHOVEL = register("holo_shovel",
            new HoloShovelItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));

    public static final HoeItem HOLO_HOE = register("holo_hoe",
            new HoloHoeItem(ToolMaterials.DIAMOND, new Item.Settings().maxCount(1)));


    public static <T extends Item> T register(String name, T item){
        return Registry.register(Registries.ITEM, NeoNix.id(name),item);
    }

    public static void load(){

    }

    public static final class ModDataComponents {
        public static ComponentType<Integer> ENERGY;
        public static ComponentType<Integer> MINED_COUNT;     // progreso hacia el trigger (0..500)
        public static ComponentType<Integer> OVERCLOCK_LEFT;  // bloques gratis restantes del overclock (0..50)

        public static void init() {
            ENERGY = Registry.register(Registries.DATA_COMPONENT_TYPE,
                    Identifier.of("neonix","energy"),
                    ComponentType.<Integer>builder().codec(Codec.INT).build());

            MINED_COUNT = Registry.register(Registries.DATA_COMPONENT_TYPE,
                    Identifier.of("neonix","mined_count"),
                    ComponentType.<Integer>builder().codec(Codec.INT).build());

            OVERCLOCK_LEFT = Registry.register(Registries.DATA_COMPONENT_TYPE,
                    Identifier.of("neonix","overclock_left"),
                    ComponentType.<Integer>builder().codec(Codec.INT).build());
        }

        public static int getEnergy(ItemStack s) { return s.getOrDefault(ENERGY, 0); }
        public static void setEnergy(ItemStack s, int v) { s.set(ENERGY, Math.max(v, 0)); }

        public static int getMined(ItemStack s) { return s.getOrDefault(MINED_COUNT, 0); }
        public static void setMined(ItemStack s, int v) { s.set(MINED_COUNT, Math.max(v, 0)); }

        public static int getOverclockLeft(ItemStack s) { return s.getOrDefault(OVERCLOCK_LEFT, 0); }
        public static void setOverclockLeft(ItemStack s, int v) { s.set(OVERCLOCK_LEFT, Math.max(v, 0)); }
    }
}
