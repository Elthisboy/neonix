package com.elthisboy.neonix.holoaxe;

import com.elthisboy.neonix.init.ItemInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemUsageContext;

/**
 * HoloAxeItem — funciona igual que los otros holo-tools: no durabilidad vanilla,
 * consume ENERGÍA al talar/minar/usar acciones de hacha, soporta overclock y recarga con HOLO_CHARGE.
 */
public class HoloAxeItem extends AxeItem {
    // Config (ajusta según quieras)
    private static final int CAPACITY = 1000;
    private static final int COST_PER_BLOCK = 1;
    private static final int TRIGGER_BLOCKS = 450;    // bloques hasta overclock
    private static final int OC_FREE_BLOCKS = 45;     // bloques gratis en OC
    private static final float OC_SPEED_MULT = 5.0f;  // multiplicador en OC
    private static final int BAR_COLOR = 0xFFA500;    // naranja neon

    // Recargas por charge
    private static final int RECHARGE_LV1 = 50;
    private static final int RECHARGE_LV2 = 125;
    private static final int RECHARGE_LV3 = 250;

    public HoloAxeItem(ToolMaterial material, Settings settings) {
        super(material, settings.maxCount(1)); // sin durabilidad vanilla, limitado a 1 por slot
    }

    /* =======================
       Helpers de inicialización (no usa getNbt/hasNbt)
       ======================= */
    private void ensureComponentsInitialized(ItemStack stack) {
        if (!stack.contains(ItemInit.ModDataComponents.ENERGY)) {
            ItemInit.ModDataComponents.setEnergy(stack, 0);
        }
        if (!stack.contains(ItemInit.ModDataComponents.MINED_COUNT)) {
            ItemInit.ModDataComponents.setMined(stack, 0);
        }
        if (!stack.contains(ItemInit.ModDataComponents.OVERCLOCK_LEFT)) {
            ItemInit.ModDataComponents.setOverclockLeft(stack, 0);
        }
    }

    /* =======================
       VELOCIDAD / CHECKS
       ======================= */
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);
        float base = super.getMiningSpeed(stack, state);
        if (ocLeft > 0) return base * OC_SPEED_MULT;
        if (energy <= 0) return 0.3f; // muy lento sin energía
        return base;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (miner.getAbilities().creativeMode) return true;
        ItemStack stack = miner.getMainHandStack();
        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);
        if (energy <= 0 && ocLeft <= 0) return false;
        return super.canMine(state, world, pos, miner);
    }

    /* =======================
       POSTMINE (para minado normal con el hacha) - no llamar a super => no durabilidad
       ======================= */
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient) {
            ensureComponentsInitialized(stack);

            int energy = ItemInit.ModDataComponents.getEnergy(stack);
            int mined  = ItemInit.ModDataComponents.getMined(stack);
            int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);

            boolean shouldCountAndConsume = !state.isAir() && state.getHardness(world, pos) > 0.0F;
            if (shouldCountAndConsume) {
                if (ocLeft > 0) {
                    ocLeft = Math.max(0, ocLeft - 1);
                } else {
                    if (energy > 0) energy = Math.max(0, energy - COST_PER_BLOCK);
                    mined += 1;
                    if (mined >= TRIGGER_BLOCKS && miner instanceof PlayerEntity p) {
                        mined = 0;
                        ocLeft = OC_FREE_BLOCKS;
                        if (p instanceof ServerPlayerEntity sp) {
                            sp.sendMessage(Text.translatable("text.neonix.overclock_on", OC_FREE_BLOCKS), true);
                        }
                    }
                }
            }

            ItemInit.ModDataComponents.setEnergy(stack, energy);
            ItemInit.ModDataComponents.setMined(stack, mined);
            ItemInit.ModDataComponents.setOverclockLeft(stack, ocLeft);
        }
        // no llamar a super -> evita que disminuya durabilidad vanilla
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // no durabilidad por golpes
        return true;
    }

    /* =======================
       useOnBlock (acciones de hacha: strip, wax, etc.) -> consumir energía si la acción tuvo efecto
       ======================= */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        // cliente: dejar que super haga los efectos visuales; servidor aplica la lógica de consumo
        if (world.isClient) {
            return super.useOnBlock(context);
        }

        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState pre = world.getBlockState(pos);

        boolean shouldCountAndConsume = !pre.isAir() && pre.getHardness(world, pos) > 0.0F;

        ensureComponentsInitialized(stack);

        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        int mined  = ItemInit.ModDataComponents.getMined(stack);
        int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);

        // si no hay energía ni OC y no es creative -> bloquar
        if (player != null && !player.getAbilities().creativeMode && energy <= 0 && ocLeft <= 0) {
            if (player instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable("text.neonix.no_energy"), true);
            }
            return ActionResult.PASS;
        }

        ActionResult res = super.useOnBlock(context);

        // aceptar cualquier resultado que no sea PASS/FAIL como "acción efectiva"
        if (res != ActionResult.PASS && res != ActionResult.FAIL && shouldCountAndConsume) {
            if (ocLeft > 0) {
                ocLeft = Math.max(0, ocLeft - 1);
            } else {
                if (energy > 0) energy = Math.max(0, energy - COST_PER_BLOCK);
                mined += 1;
                if (mined >= TRIGGER_BLOCKS && player instanceof ServerPlayerEntity sp) {
                    mined = 0;
                    ocLeft = OC_FREE_BLOCKS;
                    sp.sendMessage(Text.translatable("text.neonix.overclock_on", OC_FREE_BLOCKS), true);
                }
            }

            ItemInit.ModDataComponents.setEnergy(stack, energy);
            ItemInit.ModDataComponents.setMined(stack, mined);
            ItemInit.ModDataComponents.setOverclockLeft(stack, ocLeft);
        }

        // garantizar que no se haya aplicado daño de durabilidad vanilla
        if (!stack.isEmpty()) stack.setDamage(0);

        return res;
    }

    /* =======================
       RECARGA CON HOLO_CHARGE (Shift + Right click)
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

    private int tryConsumeChargeAndRecharge(PlayerEntity player, ItemStack axe) {
        int current = ItemInit.ModDataComponents.getEnergy(axe);
        if (current >= CAPACITY) return 0;

        int add = 0;
        if (consumeOne(player, ItemInit.HOLO_CHARGE_LV3)) add = RECHARGE_LV3;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV2)) add = RECHARGE_LV2;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV1)) add = RECHARGE_LV1;

        if (add > 0) {
            int room = CAPACITY - current;
            int realAdd = Math.min(room, add);
            ItemInit.ModDataComponents.setEnergy(axe, current + realAdd);
            return realAdd;
        }
        return 0;
    }

    private boolean consumeOne(PlayerEntity player, Item item) {
        ItemStack off = player.getOffHandStack();
        if (!off.isEmpty() && off.isOf(item)) {
            off.decrement(1);
            return true;
        }
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
       BARRA DEL ITEM = ENERGÍA
       ======================= */
    @Override public boolean isItemBarVisible(ItemStack stack) { return true; }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        return Math.round(13f * Math.min(1f, Math.max(0f, energy / (float) CAPACITY)));
    }

    @Override public int getItemBarColor(ItemStack stack) { return BAR_COLOR; }

    /* =======================
       ENCANTABLE
       ======================= */
    @Override public boolean isEnchantable(ItemStack stack) { return stack.getCount() == 1; }
    @Override public int getEnchantability() { return 10; }
}
