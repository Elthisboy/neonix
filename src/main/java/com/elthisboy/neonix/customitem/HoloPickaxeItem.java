package com.elthisboy.neonix.customitem;

import com.elthisboy.neonix.init.ModDataComponents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoloPickaxeItem extends PickaxeItem {
    private static final int CAPACITY = 150;
    private static final int COST_PER_BLOCK = 1;
    private static final int RECHARGE_PER_REDSTONE = 50;
    private static final int BAR_COLOR = 0x00E5FF; // cian

    // Constructor 1.21.x: solo (ToolMaterial, Item.Settings)
    public HoloPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient) {
            // init defensivo por si falta componente en stacks viejos
            if (!stack.contains(ModDataComponents.ENERGY)) {
                ModDataComponents.setEnergy(stack, CAPACITY);
            }
            if (!stack.contains(ModDataComponents.MINED_COUNT)) {
                ModDataComponents.setMined(stack, 0);
            }

            int energy = ModDataComponents.getEnergy(stack);
            int mined  = ModDataComponents.getMined(stack);

            // consumo (si hay energía)
            if (energy > 0) energy = Math.max(0, energy - COST_PER_BLOCK);

            // conteo (para overclock externo si lo usas)
            mined += 1;
            ModDataComponents.setEnergy(stack, energy);
            ModDataComponents.setMined(stack, mined);

            // si quieres disparar Overclock aquí, llama a OverclockState.start(...) cuando toque
            if (miner instanceof PlayerEntity p && mined >= 100) {
                ModDataComponents.setMined(stack, 0);
                OverclockState.start(p, 8, 3.0f); // 8s, x3 (tu implementación)
            }
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    // Click derecho: SHIFT + click derecho consume 1 redstone para recargar.
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        if (user.isSneaking()) {
            // Buscar 1 redstone en inventario
            int slot = user.getInventory().getSlotWithStack(new ItemStack(net.minecraft.item.Items.REDSTONE));
            if (slot >= 0) {
                int energy = ModDataComponents.getEnergy(stack);
                if (energy < CAPACITY) {
                    user.getInventory().removeStack(slot, 1);
                    ModDataComponents.setEnergy(stack, Math.min(CAPACITY, energy + RECHARGE_PER_REDSTONE));
                    return TypedActionResult.success(stack, false);
                }
            }
            return TypedActionResult.pass(stack);
        }

        return TypedActionResult.pass(stack);
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient || !selected || !(entity instanceof ServerPlayerEntity player)) return;

        // aseguramos que realmente sea la mano principal
        if (!player.getMainHandStack().equals(stack)) return;

        // contador +1 cada vez que el item está seleccionado en el tick
        int mined = ModDataComponents.getMined(stack);
        ModDataComponents.setMined(stack, mined + 1);

        // mostrar en actionbar el valor actual del contador
        player.sendMessage(Text.literal("Contador: " + (mined + 1)), true);

    }


    // Barra de "durabilidad" mostrando la ENERGÍA.
    @Override
    public boolean isItemBarVisible(ItemStack stack) { return true; }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int energy = ModDataComponents.getEnergy(stack);
        return Math.round(13f * Math.min(1f, Math.max(0f, energy / (float) CAPACITY)));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return BAR_COLOR;
    }




}