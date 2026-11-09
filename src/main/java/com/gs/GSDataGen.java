package com.gs;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import java.util.concurrent.CompletableFuture;
import com.gs.Items.ModItemIDs;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider.ItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

public class GSDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(GSServerModTagProvider::new);
    }

    public class GSServerModTagProvider extends ItemTagsProvider {
        public GSServerModTagProvider(FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            builder(ItemTags.SPEARS).add(ModItemIDs.ASPECT_OF_THE_VOID);
        }
    }

}
