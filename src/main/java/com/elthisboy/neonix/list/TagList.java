package com.elthisboy.neonix.list;

import com.elthisboy.neonix.NeoNix;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;


public class TagList {

    public static class Blocks {
        public static final TagKey<Block> EXAMPLE_TAG = TagKey.of(RegistryKeys.BLOCK, NeoNix.id("example"));


        //public static final TagKey<Block> NEEDS_PINK_GARNET_TOOL = createTag("needs_pink_garnet_tool");
        public static final TagKey<Block> INCORRECT_FOR_EXAMPLE_TOOL = createTag("incorrect_for_example_tool");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(NeoNix.MOD_ID, name));
        }
    }
    }
}
