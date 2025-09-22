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



        // Lv1:
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV1, 1)
                .pattern("R")
                .pattern("Q")
                .input('R', Items.REDSTONE)
                .input('Q', Items.QUARTZ)
                .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
                .offerTo(recipeExporter);

        // Lv2:
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV2, 1)
                .pattern("C C")
                .pattern(" B ")
                .pattern("C C")
                .input('C', ItemInit.HOLO_CHARGE_LV1)
                .input('B', Items.REDSTONE_BLOCK)
                .criterion("has_lv1", conditionsFromItem(ItemInit.HOLO_CHARGE_LV1))
                .offerTo(recipeExporter);

        // Lv3:
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemInit.HOLO_CHARGE_LV3, 1)
                .pattern("C C")
                .pattern(" B ")
                .pattern(" D ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('B', Items.REDSTONE_BLOCK)
                .input('D', Items.DIAMOND)
                .criterion("has_lv2", conditionsFromItem(ItemInit.HOLO_CHARGE_LV2))
                .offerTo(recipeExporter);



        // HOLO_PICKAXE:
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_PICKAXE, 1)
                .pattern("CBC")
                .pattern(" P ")
                .pattern(" C ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('P', Items.DIAMOND_PICKAXE)
                .input('B', Items.REDSTONE_BLOCK)
                .criterion("has_diamond_pickaxe", conditionsFromItem(Items.DIAMOND_PICKAXE))
                .offerTo(recipeExporter);

        // HOLO_SHOVEL:
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_SHOVEL, 1)
                .pattern(" C ")
                .pattern(" S ")
                .pattern(" C ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('S', Items.DIAMOND_SHOVEL)
                .criterion("has_diamond_shovel", conditionsFromItem(Items.DIAMOND_SHOVEL))
                .offerTo(recipeExporter);

        // HOLO_AXE:
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_AXE, 1)
                .pattern(" C ")
                .pattern(" A ")
                .pattern(" C ")
                .input('A', Items.DIAMOND_AXE)
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .criterion("has_diamond_axe", conditionsFromItem(Items.DIAMOND_AXE))
                .offerTo(recipeExporter);

        // HOLO_HOE:
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemInit.HOLO_HOE, 1)
                .pattern(" C ")
                .pattern(" H ")
                .pattern(" C ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('H', Items.DIAMOND_HOE)
                .criterion("has_diamond_hoe", conditionsFromItem(Items.DIAMOND_HOE))
                .offerTo(recipeExporter);

        // HOLO_SWORD:
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ItemInit.HOLO_SWORD, 1)
                .pattern("CBC")
                .pattern(" C ")
                .pattern(" S ")
                .input('C', ItemInit.HOLO_CHARGE_LV2)
                .input('S', Items.DIAMOND_SWORD)
                .input('B', Items.REDSTONE_BLOCK)
                .criterion("has_diamond_sword", conditionsFromItem(Items.DIAMOND_SWORD))
                .offerTo(recipeExporter);
    }


    }


