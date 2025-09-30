package com.elthisboy.neonix.data.provider;

import com.elthisboy.neonix.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class NeoNixProvider extends FabricModelProvider {
    public NeoNixProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ItemInit.HOLO_CHARGE_LV1, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_CHARGE_LV2, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_CHARGE_LV3, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_AXE, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_PICKAXE, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_HOE, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_SHOVEL, Models.GENERATED);
        itemModelGenerator.register(ItemInit.HOLO_SWORD, Models.GENERATED);


    }
}
