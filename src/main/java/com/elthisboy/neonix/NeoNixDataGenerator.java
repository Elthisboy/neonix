package com.elthisboy.neonix;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import com.elthisboy.neonix.data.provider.*;


public class NeoNixDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(NeoNixBlockTableProvider::new);
		pack.addProvider(NeoNixLanguageProvider::new);
		pack.addProvider(NeoNixBlockTagProvider::new);
		pack.addProvider(NeoNixItemTagProvider::new);
		pack.addProvider(NeoNixProvider::new);
		pack.addProvider(NeoNixRecipeProvider::new);
	}
}
