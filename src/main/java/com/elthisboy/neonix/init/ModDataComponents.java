package com.elthisboy.neonix.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModDataComponents {
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
