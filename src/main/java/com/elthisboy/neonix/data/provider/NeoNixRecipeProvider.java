package com.elthisboy.neonix.data.provider;

import com.elthisboy.neonix.NeoNix;
import com.elthisboy.neonix.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class NeoNixRecipeProvider extends FabricRecipeProvider {
    public NeoNixRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {

        /* ==========================
           HOLO CHARGES (energía)
           ========================== */

        // Lv1 (barato): vertical 1x2
        // R
        // Q
        // R = Redstone, Q = Nether Quartz
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV1, 1)
                .pattern("R")
                .pattern("Q")
                .input('R', Items.REDSTONE)
                .input('Q', Items.QUARTZ)
                .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
                .offerTo(recipeExporter);

        // Lv2: 4x Lv1 en las esquinas + redstone block al centro
        // C C
        //  B
        // C C
        // C = Lv1, B = Redstone Block
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV2, 1)
                .pattern("C C")
                .pattern(" B ")
                .pattern("C C")
                .input('C', ItemInit.HOLO_CHARGE_LV1)
                .input('B', Items.REDSTONE_BLOCK)
                .criterion("has_lv1", conditionsFromItem(ItemInit.HOLO_CHARGE_LV1))
                .offerTo(recipeExporter);

        // Lv3: 2x Lv2 arriba, redstone block centro, diamond abajo
        // C C
        //  B
        //  D
        // C = Lv2, B = Redstone Block, D = Diamond
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV3, 1)
                .pattern("C C")
                .pattern(" B ")
                .pattern(" D ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('B', Items.REDSTONE_BLOCK)
                .input('D', Items.DIAMOND)
                .criterion("has_lv2", conditionsFromItem(ItemInit.HOLO_CHARGE_LV2))
                .offerTo(recipeExporter);


        /* ======================================
           “Upgrade” de herramientas a HOLO
           (consume la herramienta de diamante)
           ====================================== */

        // HOLO_PICKAXE:
        // C P C
        //  C
        // C = Lv2, P = Diamond Pickaxe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_PICKAXE, 1)
                .pattern("CPC")
                .pattern(" C ")
                .pattern("   ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('P', Items.DIAMOND_PICKAXE)
                .criterion("has_diamond_pickaxe", conditionsFromItem(Items.DIAMOND_PICKAXE))
                .offerTo(recipeExporter);

        // HOLO_SHOVEL:
        //  C
        //  S
        // C = Lv2, S = Diamond Shovel
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_SHOVEL, 1)
                .pattern(" C ")
                .pattern(" S ")
                .pattern("   ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('S', Items.DIAMOND_SHOVEL)
                .criterion("has_diamond_shovel", conditionsFromItem(Items.DIAMOND_SHOVEL))
                .offerTo(recipeExporter);

        // HOLO_AXE:
        // A C
        // A
        //  C
        // A = Diamond Axe, C = Lv2
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_AXE, 1)
                .pattern("AC ")
                .pattern("A  ")
                .pattern(" C ")
                .input('A', Items.DIAMOND_AXE)
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .criterion("has_diamond_axe", conditionsFromItem(Items.DIAMOND_AXE))
                .offerTo(recipeExporter);

        // HOLO_HOE:
        //  C
        //  H
        // C = Lv2, H = Diamond Hoe
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_HOE, 1)
                .pattern(" C ")
                .pattern(" H ")
                .pattern("   ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('H', Items.DIAMOND_HOE)
                .criterion("has_diamond_hoe", conditionsFromItem(Items.DIAMOND_HOE))
                .offerTo(recipeExporter);

        // HOLO_SWORD:
        // C C
        //  S
        //  C
        // C = Lv2, S = Diamond Sword
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ItemInit.HOLO_SWORD, 1)
                .pattern("C C")
                .pattern(" S ")
                .pattern(" C ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('S', Items.DIAMOND_SWORD)
                .criterion("has_diamond_sword", conditionsFromItem(Items.DIAMOND_SWORD))
                .offerTo(recipeExporter);
    }


    }


