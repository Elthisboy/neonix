package com.elthisboy.neonix.customitem;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class SidebarDebug {
    private static final String OBJ = "neonix_dbg";

    public static void show(ServerPlayerEntity p, int capacity, int energy, int mined, int trigger, int ocLeft, int ocMax) {
        MinecraftServer server = p.getServer();
        ServerCommandSource src = server.getCommandSource().withLevel(2).withSilent(); // silencioso en chat

        // 1) Crear objective si no existe (no falla si ya existe)
        exec(server, src, "scoreboard objectives add " + OBJ + " dummy \"Neo-Nix Debug\"");

        // 2) Mostrar en el sidebar
        exec(server, src, "scoreboard objectives setdisplay sidebar " + OBJ);

        // 3) Limpiar líneas antiguas de este objective
        exec(server, src, "scoreboard players reset * " + OBJ);

        // 4) Escribir líneas (orden determinado por la puntuación: mayor arriba)
        String l1 = String.format("Energía: %d/%d", energy, capacity);
        String l2 = String.format("Progreso: %d/%d", mined, trigger);
        String l3 = (ocLeft > 0)
                ? String.format("Overclock: ACTIVO (%d/%d)", ocLeft, ocMax)
                : String.format("Overclock: %d%%", Math.min(99, (int)(100.0 * mined / Math.max(1, trigger))));

        // comillas para permitir espacios
        exec(server, src, "scoreboard players set \"" + l1 + "\" " + OBJ + " 3");
        exec(server, src, "scoreboard players set \"" + l2 + "\" " + OBJ + " 2");
        exec(server, src, "scoreboard players set \"" + l3 + "\" " + OBJ + " 1");
    }

    public static void hide(ServerPlayerEntity p) {
        MinecraftServer server = p.getServer();
        ServerCommandSource src = server.getCommandSource().withLevel(2).withSilent();
        // Quita el sidebar (deja el slot vacío)
        exec(server, src, "scoreboard objectives setdisplay sidebar");
        // (opcional) limpiar jugadores/entradas del objective
        exec(server, src, "scoreboard players reset * " + OBJ);
    }

    private static void exec(MinecraftServer server, ServerCommandSource src, String cmd) {
        server.getCommandManager().executeWithPrefix(src, cmd);
    }
}