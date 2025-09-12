package com.elthisboy.neonix;

import net.fabricmc.api.ModInitializer;
import com.elthisboy.neonix.init.*;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoNix implements ModInitializer {
	public static final String MOD_ID = "neonix";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");

		ItemInit.load();
		BlockInit.load();
		ItemGroupInit.load();


		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier id(String path){
		return Identifier.of(MOD_ID,path);
	}
}