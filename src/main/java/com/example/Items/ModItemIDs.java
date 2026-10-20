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
    public static final ResourceKey<Item> PRIZE_BOX = create("prize_box");
}