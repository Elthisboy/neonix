package com.elthisboy.neonix.init;

import com.elthisboy.neonix.customitem.OverclockState;
import com.elthisboy.neonix.init.ModDataComponents;
import com.elthisboy.neonix.init.ItemInit;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.WeakHashMap;

public class ActionbarHandler {


    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                ItemStack held = p.getMainHandStack();
                if (held.isOf(ItemInit.HOLO_PICK)) {
                    // Mensaje simple en el actionbar mientras sostienes el ítem
                    p.sendMessage(Text.literal("Neo-Nix • ActionBar TEST"), true);
                }
            }
        });
    }
    public static void load() {
    }
}