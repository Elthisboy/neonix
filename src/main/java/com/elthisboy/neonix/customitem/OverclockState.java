// OverclockState.java
package com.elthisboy.neonix.customitem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.WeakHashMap;

public final class OverclockState {
    private static final WeakHashMap<PlayerEntity, Long> UNTIL = new WeakHashMap<>();
    private static final WeakHashMap<PlayerEntity, Float> MULTIPLIER = new WeakHashMap<>();

    public static void start(PlayerEntity player, int seconds, float speedMultiplier) {
        long endTick = player.getWorld().getTime() + seconds * 20L;
        UNTIL.put(player, endTick);
        MULTIPLIER.put(player, speedMultiplier);

        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity sp) {
            // feedback visual (placeholder)
            int amplifier = Math.max(0, (int)(speedMultiplier - 1));
            sp.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, seconds * 20, amplifier, false, false));
        }
    }

    public static boolean isActive(PlayerEntity player) {
        Long until = UNTIL.get(player);
        if (until == null) return false;
        if (player.getWorld().getTime() > until) {
            clear(player);
            return false;
        }
        return true;
    }

    public static float getMultiplier(PlayerEntity player) {
        return isActive(player) ? MULTIPLIER.getOrDefault(player, 1.0f) : 1.0f;
    }

    // ticks restantes (0 si no activo)
    public static long getRemainingTicks(PlayerEntity player) {
        Long until = UNTIL.get(player);
        if (until == null) return 0L;
        long rem = until - player.getWorld().getTime();
        return Math.max(0L, rem);
    }

    public static void clear(PlayerEntity player) {
        UNTIL.remove(player);
        MULTIPLIER.remove(player);
    }
}
