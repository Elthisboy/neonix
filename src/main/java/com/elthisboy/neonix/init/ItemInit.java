package com.elthisboy.neonix.init;

import com.elthisboy.neonix.NeoNix;
import com.elthisboy.neonix.holoaxe.HoloAxeItem;
import com.elthisboy.neonix.holohoe.HoloHoeItem;
import com.elthisboy.neonix.holopickaxe.HoloPickaxeItem;
import com.elthisboy.neonix.holoshovel.HoloShovelItem;
import com.elthisboy.neonix.holosword.HoloSwordItem;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ItemInit {

    // Helper
    private static Item.Settings withLore(Item.Settings s, Text... lines) {
        return s.component(DataComponentTypes.LORE, new LoreComponent(java.util.List.of(lines)));
    }

    private static Item.Settings withName(Item.Settings base, String translationKey, Formatting color) {
        return base.component(
                DataComponentTypes.CUSTOM_NAME,
                Text.translatable(translationKey).formatted(color).styled(s -> s.withItalic(false))
        );
    }

    public static final Item HOLO_CHARGE_LV1 = register("holo_charge_lv1",
            new Item(
                    withName(
                            withLore(new Item.Settings(),
                                    Text.translatable("lore.neonix.charge").formatted(Formatting.AQUA),
                                    Text.translatable("lore.neonix.charge.lv1").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_charge_lv1", Formatting.GOLD
                    )
            )
    );

    public static final Item HOLO_CHARGE_LV2 = register("holo_charge_lv2",
            new Item(
                    withName(
                            withLore(new Item.Settings(),
                                    Text.translatable("lore.neonix.charge").formatted(Formatting.AQUA),
                                    Text.translatable("lore.neonix.charge.lv2").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_charge_lv2", Formatting.GOLD
                    )
            )
    );

    public static final Item HOLO_CHARGE_LV3 = register("holo_charge_lv3",
            new Item(
                    withName(
                            withLore(new Item.Settings(),
                                    Text.translatable("lore.neonix.charge").formatted(Formatting.AQUA),
                                    Text.translatable("lore.neonix.charge.lv3").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_charge_lv3", Formatting.GOLD
                    )
            )
    );

    // Holo Pickaxe
    public static final PickaxeItem HOLO_PICKAXE = register("holo_pickaxe",
            new HoloPickaxeItem(ToolMaterials.DIAMOND,
                    withName(
                            withLore(new Item.Settings().maxCount(1),
                                    Text.translatable("lore.neonix.holo").formatted(Formatting.DARK_AQUA),
                                    Text.translatable("lore.neonix.energy_based").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_pickaxe", Formatting.AQUA
                    )
            )
    );

    // Holo Sword
    public static final SwordItem HOLO_SWORD = register("holo_sword",
            new HoloSwordItem(ToolMaterials.DIAMOND,
                    withName(
                            withLore(new Item.Settings().maxCount(1),
                                    Text.translatable("lore.neonix.holo").formatted(Formatting.DARK_AQUA),
                                    Text.translatable("lore.neonix.sword_hint").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_sword", Formatting.AQUA
                    )
            )
    );

    // Holo Axe
    public static final AxeItem HOLO_AXE = register("holo_axe",
            new HoloAxeItem(ToolMaterials.DIAMOND,
                    withName(
                            withLore(new Item.Settings().maxCount(1),
                                    Text.translatable("lore.neonix.holo").formatted(Formatting.DARK_AQUA),
                                    Text.translatable("lore.neonix.energy_based").formatted(Formatting.GRAY),
                                    Text.translatable("lore.neonix.axe_hint").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_axe", Formatting.AQUA
                    )
            )
    );

    // Holo Shovel
    public static final ShovelItem HOLO_SHOVEL = register("holo_shovel",
            new HoloShovelItem(ToolMaterials.DIAMOND,
                    withName(
                            withLore(new Item.Settings().maxCount(1),
                                    Text.translatable("lore.neonix.holo").formatted(Formatting.DARK_AQUA),
                                    Text.translatable("lore.neonix.energy_based").formatted(Formatting.GRAY),
                                    Text.translatable("lore.neonix.shovel_hint").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_shovel", Formatting.AQUA
                    )
            )
    );

    // Holo Hoe
    public static final HoeItem HOLO_HOE = register("holo_hoe",
            new HoloHoeItem(ToolMaterials.DIAMOND,
                    withName(
                            withLore(new Item.Settings().maxCount(1),
                                    Text.translatable("lore.neonix.holo").formatted(Formatting.DARK_AQUA),
                                    Text.translatable("lore.neonix.energy_based").formatted(Formatting.GRAY),
                                    Text.translatable("lore.neonix.hoe_hint").formatted(Formatting.GRAY)
                            ),
                            "item.neonix.holo_hoe", Formatting.AQUA
                    )
            )
    );


    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, NeoNix.id(name), item);
    }

    public static void load() {
        ModDataComponents.init();
    }


    // Data Components compartidos
    public static final class ModDataComponents {
        public static ComponentType<Integer> ENERGY;
        public static ComponentType<Integer> MINED_COUNT;     // progreso (o hits) hacia trigger
        public static ComponentType<Integer> OVERCLOCK_LEFT;  // bloques/hits gratis en OC

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