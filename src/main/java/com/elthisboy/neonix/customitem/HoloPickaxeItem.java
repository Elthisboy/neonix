package com.elthisboy.neonix.customitem;

import com.elthisboy.neonix.init.ModDataComponents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoloPickaxeItem extends PickaxeItem {
    // Config
    private static final int CAPACITY = 5;
    private static final int COST_PER_BLOCK = 1;
    private static final int RECHARGE_PER_REDSTONE = 50;
    private static final int TRIGGER_BLOCKS = 500;     // cada 500 bloques -> overclock
    private static final int OC_FREE_BLOCKS = 50;      // bloques gratis y boost
    private static final float OC_SPEED_MULT = 3.0f;   // x3 más rápido
    private static final int BAR_COLOR = 0x00E5FF;

    public HoloPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    // Velocidad: si no hay energía y no hay overclock → muy lento. Con overclock → x3.
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        int energy = ModDataComponents.getEnergy(stack);
        int ocLeft = ModDataComponents.getOverclockLeft(stack);
        float base = super.getMiningSpeed(stack, state);

        if (ocLeft > 0) return base * OC_SPEED_MULT;
        if (energy <= 0) return 0.3f; // “muy lento” sin energía
        return base;
    }

    // Bloquea inicio de minado si no hay energía y no hay overclock (excepto creativo)
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (miner.getAbilities().creativeMode) return true;
        ItemStack stack = miner.getMainHandStack();
        int energy = ModDataComponents.getEnergy(stack);
        int ocLeft = ModDataComponents.getOverclockLeft(stack);
        if (energy <= 0 && ocLeft <= 0) return false;
        return super.canMine(state, world, pos, miner);
    }

    // Consumo/gatillo de overclock
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient) {
            // init defensivo
            if (!stack.contains(ModDataComponents.ENERGY))        ModDataComponents.setEnergy(stack, CAPACITY);
            if (!stack.contains(ModDataComponents.MINED_COUNT))   ModDataComponents.setMined(stack, 0);
            if (!stack.contains(ModDataComponents.OVERCLOCK_LEFT))ModDataComponents.setOverclockLeft(stack, 0);

            int energy = ModDataComponents.getEnergy(stack);
            int mined  = ModDataComponents.getMined(stack);
            int ocLeft = ModDataComponents.getOverclockLeft(stack);

            // === criterio estilo vanilla (gasta si el bloque realmente "cuesta" romper) ===
            // - no es aire
            // - hardness > 0  (dirt/sand/leaves ~> consumen; short_grass/flowers = 0 => NO consume)
            boolean shouldCountAndConsume = !state.isAir() && state.getHardness(world, pos) > 0.0F;

            if (shouldCountAndConsume) {
                if (ocLeft > 0) {
                    // Overclock activo: no gasta energía, consume 1 bloque gratis
                    ocLeft = Math.max(0, ocLeft - 1);
                } else {
                    if (energy > 0) energy = Math.max(0, energy - COST_PER_BLOCK);
                }

                // progreso hacia overclock
                mined += 1;

                // activar overclock cada 500
                if (mined >= TRIGGER_BLOCKS && miner instanceof PlayerEntity p) {
                    mined = 0;
                    ocLeft = OC_FREE_BLOCKS; // 50 bloques sin gasto + boost (se aplica en getMiningSpeed)
                    if (p instanceof ServerPlayerEntity sp) {
                        sp.sendMessage(Text.literal("§bOVERCLOCK §factivado: §e" + OC_FREE_BLOCKS + " §fbloques gratis"), true);
                    }
                }
            }

            // guardar
            ModDataComponents.setEnergy(stack, energy);
            ModDataComponents.setMined(stack,  mined);
            ModDataComponents.setOverclockLeft(stack, ocLeft);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    // Recarga con redstone (Shift+Click derecho)
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        if (user.isSneaking()) {
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

    // Sidebar de debug SOLO si el pico está en mano
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof ServerPlayerEntity p)) return;
        if (slot != p.getInventory().selectedSlot) return;

        if (p.getMainHandStack() == stack) {
            int energy = ModDataComponents.getEnergy(stack);
            int mined  = ModDataComponents.getMined(stack);
            int ocLeft = ModDataComponents.getOverclockLeft(stack);
            SidebarDebug.show(p, CAPACITY, energy, mined, TRIGGER_BLOCKS, ocLeft, OC_FREE_BLOCKS);
        } else {
            SidebarDebug.hide(p);
        }
    }

    // Barra del item = energía
    @Override
    public boolean isItemBarVisible(ItemStack stack) { return true; }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int energy = ModDataComponents.getEnergy(stack);
        return Math.round(13f * Math.min(1f, Math.max(0f, energy / (float) CAPACITY)));
    }

    @Override
    public int getItemBarColor(ItemStack stack) { return BAR_COLOR; }
}