package com.example.Items;

import com.example.TemplateMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIDs {
    public static ResourceKey<Item> create(String name) {
        // Create the item key.
        return ResourceKey.create(Registries.ITEM, TemplateMod.id(name));
    }

    //Items
    public static final ResourceKey<Item> PRIZE_TICKET = create("prize_ticket");
    public static final ResourceKey<Item> WHEEL_TOKEN = create("wheel_token");
    public static final ResourceKey<Item> ARTIFACT_VOUCHER = create("artifact_voucher");
    public static final ResourceKey<Item> ENERGY_CUBE = create("energy_cube");
    public static final ResourceKey<Item> CROWN = create("crown");
    public static final ResourceKey<Item> ASTRO_COIN = create("astro_coin");
}