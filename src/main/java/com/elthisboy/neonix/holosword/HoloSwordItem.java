package com.elthisboy.neonix.holosword;

import com.elthisboy.neonix.init.ItemInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoloSwordItem extends SwordItem {
    // Config
    private static final int CAPACITY = 1000;          // energía máx
    private static final int COST_PER_HIT = 2;        // gasto por golpe
    private static final int TRIGGER_HITS = 300;      // cada 300 hits → overclock
    private static final int OC_FREE_HITS = 40;       // hits gratis en OC
    private static final float OC_DAMAGE_MULT = 2.5f; // daño x2.5 en OC
    private static final int BAR_COLOR = 0xFF004C;    // color barra

    // “Sensación” de espada de diamante sin tocar atributos del item:
    private static final float DIAMOND_BASE_DAMAGE = 8.0f;
    private static final int   DIAMOND_COOLDOWN_TICKS = 12; // ~0.6s aprox

    // Recargas por charge
    private static final int RECHARGE_LV1 = 50;
    private static final int RECHARGE_LV2 = 125;
    private static final int RECHARGE_LV3 = 250;

    public HoloSwordItem(ToolMaterial material, Settings settings) {
        super(material, settings.maxCount(1));
    }

    /* =======================
       INIT DEFENSIVO (0 energía)
       ======================= */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (!stack.contains(ItemInit.ModDataComponents.ENERGY))         ItemInit.ModDataComponents.setEnergy(stack, 0);
            if (!stack.contains(ItemInit.ModDataComponents.MINED_COUNT))    ItemInit.ModDataComponents.setMined(stack, 0);
            if (!stack.contains(ItemInit.ModDataComponents.OVERCLOCK_LEFT)) ItemInit.ModDataComponents.setOverclockLeft(stack, 0);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    /* =======================
       COMBATE / ENERGÍA / DAÑO
       ======================= */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // No llamamos a super => sin durabilidad vanilla
        if (!attacker.getWorld().isClient) {
            // Asegurar componentes (iniciales = 0)
            if (!stack.contains(ItemInit.ModDataComponents.ENERGY))         ItemInit.ModDataComponents.setEnergy(stack, 0);
            if (!stack.contains(ItemInit.ModDataComponents.MINED_COUNT))    ItemInit.ModDataComponents.setMined(stack, 0);
            if (!stack.contains(ItemInit.ModDataComponents.OVERCLOCK_LEFT)) ItemInit.ModDataComponents.setOverclockLeft(stack, 0);

            int energy = ItemInit.ModDataComponents.getEnergy(stack);
            int hits   = ItemInit.ModDataComponents.getMined(stack);
            int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);

            // Cooldown “como espada”
            if (attacker instanceof PlayerEntity p) {
                p.getItemCooldownManager().set(this, DIAMOND_COOLDOWN_TICKS);
            }

            // Determinar daño deseado para este golpe
            float desiredDamage;
            if (ocLeft > 0) {
                // Overclock: no gasta energía y daño multiplicado
                ocLeft = Math.max(0, ocLeft - 1);
                desiredDamage = DIAMOND_BASE_DAMAGE * OC_DAMAGE_MULT;
                // No sumar progreso durante OC
            } else {
                if (energy > 0) {
                    energy = Math.max(0, energy - COST_PER_HIT);
                    hits += 1;
                    desiredDamage = DIAMOND_BASE_DAMAGE;

                    // activar OC si corresponde
                    if (hits >= TRIGGER_HITS && attacker instanceof PlayerEntity p) {
                        hits = 0;
                        ocLeft = OC_FREE_HITS;
                        if (p instanceof ServerPlayerEntity sp) {
                            sp.sendMessage(Text.translatable("text.neonix.overclock_on", OC_FREE_HITS), true);
                        }
                    }
                } else {
                    // Sin energía y sin OC: no forzamos daño extra (golpe “flojo”)
                    desiredDamage = 0f;
                }
            }

            // Aplicar daño extra para alcanzar el daño “de diamante” (o su versión OC)
            if (desiredDamage > 0f && attacker instanceof PlayerEntity p) {
                double baseNow = p.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float extra = Math.max(0f, desiredDamage - (float) baseNow);
                if (extra > 0f) {
                    target.damage(p.getDamageSources().playerAttack(p), extra);
                }
            }

            // Guardar estado
            ItemInit.ModDataComponents.setEnergy(stack, energy);
            ItemInit.ModDataComponents.setMined(stack,  hits);
            ItemInit.ModDataComponents.setOverclockLeft(stack, ocLeft);
        }
        return true;
    }

    /*
      Evitar durabilidad por romper bloques (telarañas, etc).
     */
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        // No llamamos a super => sin daño de durabilidad
        return true;
    }

    /* =======================
       RECARGA (Shift + Click derecho)
       ======================= */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        if (ItemInit.ModDataComponents.getEnergy(stack) >= CAPACITY) {
            if (user instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable("text.neonix.energy_max", CAPACITY), true);
            }
            return TypedActionResult.pass(stack);
        }

        if (user.isSneaking()) {
            int added = tryConsumeChargeAndRecharge(user, stack);
            if (added > 0) {
                if (user instanceof ServerPlayerEntity sp) {
                    int energy = ItemInit.ModDataComponents.getEnergy(stack);
                    sp.sendMessage(Text.translatable("text.neonix.recharge", added, energy, CAPACITY), true);
                }
                return TypedActionResult.success(stack, false);
            } else {
                if (user instanceof ServerPlayerEntity sp) {
                    sp.sendMessage(Text.translatable("text.neonix.no_charge"), true);
                }
                return TypedActionResult.pass(stack);
            }
        }
        return TypedActionResult.pass(stack);
    }

    private int tryConsumeChargeAndRecharge(PlayerEntity player, ItemStack sword) {
        int current = ItemInit.ModDataComponents.getEnergy(sword);
        if (current >= CAPACITY) return 0;

        int add = 0;
        if (consumeOne(player, ItemInit.HOLO_CHARGE_LV3)) add = RECHARGE_LV3;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV2)) add = RECHARGE_LV2;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV1)) add = RECHARGE_LV1;

        if (add > 0) {
            int room = CAPACITY - current;
            int realAdd = Math.min(room, add);
            ItemInit.ModDataComponents.setEnergy(sword, current + realAdd);
            return realAdd;
        }
        return 0;
    }

    private boolean consumeOne(PlayerEntity player, Item item) {
        // 1) offhand
        ItemStack off = player.getOffHandStack();
        if (!off.isEmpty() && off.isOf(item)) {
            off.decrement(1);
            return true;
        }
        // 2) inventario
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(item)) {
                s.decrement(1);
                return true;
            }
        }
        return false;
    }

    /* =======================
       BARRA / ENCANTABLE
       ======================= */
    @Override public boolean isItemBarVisible(ItemStack stack) { return true; }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        return Math.round(13f * Math.min(1f, Math.max(0f, energy / (float) CAPACITY)));
    }

    @Override public int getItemBarColor(ItemStack stack) { return BAR_COLOR; }

    @Override public boolean isEnchantable(ItemStack stack) { return stack.getCount() == 1; }
    @Override public int getEnchantability() { return 12; } // cercano a diamante
}
