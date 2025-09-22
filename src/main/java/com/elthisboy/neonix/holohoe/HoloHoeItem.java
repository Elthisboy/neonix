package com.elthisboy.neonix.holohoe;

import com.elthisboy.neonix.init.ItemInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * HoloHoeItem - versión segura que no autorrrellena energía.
 * - Si el componente ENERGY no existe, se inicializa a 0 (no CAPACITY).
 * - Nunca llama a super.postMine para evitar durabilidad vanilla.
 */
public class HoloHoeItem extends HoeItem {
    // Config (paralelo al pickaxe)
    private static final int CAPACITY = 1000;        // energía máxima (tope)
    private static final int COST_PER_BLOCK = 1;     // consumo por bloque "real"
    private static final int TRIGGER_BLOCKS = 500;   // cada 500 -> overclock
    private static final int OC_FREE_BLOCKS = 50;    // bloques “gratis” en OC
    private static final float OC_SPEED_MULT = 5.0f; // multiplicador (si se usa)
    private static final int BAR_COLOR = 0x00E5FF;   // cian

    // Recargas por charge
    private static final int RECHARGE_LV1 = 50;
    private static final int RECHARGE_LV2 = 125;
    private static final int RECHARGE_LV3 = 250;

    public HoloHoeItem(ToolMaterial material, Settings settings) {
        // sin durabilidad vanilla; limitar a 1 por stack
        super(material, settings.maxCount(1));
    }

    /* =======================
       Helpers de inicialización (SEGURO: no utiliza getNbt()/hasNbt())
       ======================= */

    /**
     * Inicializa componentes defensivamente sin crear o consultar NBT directo.
     * Si falta ENERGY se inicializa a 0 (no a CAPACITY) para evitar rellenos mágicos.
     */
    private void ensureComponentsInitialized(ItemStack stack) {
        if (!stack.contains(ItemInit.ModDataComponents.ENERGY)) {
            // <<--- cambiado: POR SEGURIDAD inicializamos a 0 (no CAPACITY)
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
        if (energy <= 0) return 0.3f; // muy lento si no hay energía
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
       POSTMINE (para otros caminos que rompan bloques)
       (NO llamar a super.postMine -> evita durabilidad vanilla)
       ======================= */
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient) {
            // inicialización defensiva SIN autorrrelleno a CAPACITY
            ensureComponentsInitialized(stack);

            int energy = ItemInit.ModDataComponents.getEnergy(stack);
            int mined  = ItemInit.ModDataComponents.getMined(stack);
            int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);

            boolean shouldCountAndConsume = !state.isAir() && state.getHardness(world, pos) > 0.0F;
            if (shouldCountAndConsume) {
                if (ocLeft > 0) {
                    ocLeft = Math.max(0, ocLeft - 1); // bloques gratis en OC
                    // NO sumar progreso durante OC
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
        // no llamar a super -> así no baja durabilidad vanilla
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // evitar daño por hits
        return true;
    }

    /* =======================
       useOnBlock: arar con control de energía + reset durabilidad
       ======================= */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        // Cliente: dejar que la super haga lo visual; servidor aplica la lógica y reseteo.
        if (world.isClient) {
            return super.useOnBlock(context);
        }

        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState preState = world.getBlockState(pos);

        // criterio estilo "gasta durabilidad" vanilla: no es aire y hardness > 0
        boolean shouldCountAndConsume = !preState.isAir() && preState.getHardness(world, pos) > 0.0F;

        // inicialización defensiva
        ensureComponentsInitialized(stack);

        int energy = ItemInit.ModDataComponents.getEnergy(stack);
        int mined  = ItemInit.ModDataComponents.getMined(stack);
        int ocLeft = ItemInit.ModDataComponents.getOverclockLeft(stack);

        // Si el jugador no está en creativo y no tiene energía ni OC -> no permitir arar
        if (player != null && !player.getAbilities().creativeMode && energy <= 0 && ocLeft <= 0) {
            if (player instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable("text.neonix.no_energy"), true);
            }
            return ActionResult.PASS;
        }

        // Llamamos a vanilla para ejecutar el arado (sonidos, bloque, etc)
        ActionResult res = super.useOnBlock(context);

        // Si la acción tuvo efecto y el bloque previo requería consumo, aplicar lógica de energía/OC
        if (res == ActionResult.SUCCESS && shouldCountAndConsume) {
            if (ocLeft > 0) {
                ocLeft = Math.max(0, ocLeft - 1); // consume bloques gratis en OC
            } else {
                if (energy > 0) energy = Math.max(0, energy - COST_PER_BLOCK);
                mined += 1;
                if (mined >= TRIGGER_BLOCKS && player instanceof ServerPlayerEntity sp) {
                    mined = 0;
                    ocLeft = OC_FREE_BLOCKS;
                    sp.sendMessage(Text.translatable("text.neonix.overclock_on", OC_FREE_BLOCKS), true);
                }
            }

            // guardar estado
            ItemInit.ModDataComponents.setEnergy(stack, energy);
            ItemInit.ModDataComponents.setMined(stack, mined);
            ItemInit.ModDataComponents.setOverclockLeft(stack, ocLeft);
        }

        // Forzar que la stack no quede con daño (evita durabilidad vanilla)
        if (!stack.isEmpty()) {
            stack.setDamage(0);
        }

        return res;
    }

    /* =======================
       RECARGA CON HOLO_CHARGE (Shift + Right click)
       ======================= */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        // Si ya está lleno no consumas nada
        if (ItemInit.ModDataComponents.getEnergy(stack) >= CAPACITY) {
            if (user instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable("text.neonix.energy_max", CAPACITY), true);
            }
            return TypedActionResult.pass(stack);
        }

        // Shift + Click derecho para recargar con HOLO_CHARGE
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

    private int tryConsumeChargeAndRecharge(PlayerEntity player, ItemStack hoe) {
        int current = ItemInit.ModDataComponents.getEnergy(hoe);
        if (current >= CAPACITY) return 0;

        int add = 0;
        if (consumeOne(player, ItemInit.HOLO_CHARGE_LV3)) add = RECHARGE_LV3;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV2)) add = RECHARGE_LV2;
        else if (consumeOne(player, ItemInit.HOLO_CHARGE_LV1)) add = RECHARGE_LV1;

        if (add > 0) {
            int room = CAPACITY - current;
            int realAdd = Math.min(room, add);
            ItemInit.ModDataComponents.setEnergy(hoe, current + realAdd);
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
        // 2) inventario (incluye hotbar)
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

    /* ENCANTABLE */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantability() {
        return 10; // similar a diamante
    }
}
