package com.gs.Items;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    //Items
    public static final Item PRIZE_TICKET = register(ModItemIDs.PRIZE_TICKET, PrizeTicket::new, new Item.Properties());
    public static final Item WHEEL_TOKEN = register(ModItemIDs.WHEEL_TOKEN, WheelToken::new, new Item.Properties());
    public static final Item ARTIFACT_VOUCHER = register(ModItemIDs.ARTIFACT_VOUCHER, ArtifactVoucher::new, new Item.Properties());
    public static final Item ENERGY_CUBE = register(ModItemIDs.ENERGY_CUBE, EnergyCube::new, new Item.Properties());
    public static final Item RUSTED_CROWN = register(ModItemIDs.RUSTED_CROWN, RustedCrown::new, new Item.Properties());
    public static final Item ASTRO_COIN = register(ModItemIDs.ASTRO_COIN, AstroCoin::new, new Item.Properties());


    public static void initialize() {
    }
}
