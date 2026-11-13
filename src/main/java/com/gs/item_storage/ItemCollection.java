package com.gs.item_storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public class ItemCollection {
    public final String collection_id;
    public final ArrayList<ItemStack> stacks;

    public ItemCollection() {
        this.collection_id = UUID.randomUUID().toString();
        stacks = new ArrayList<>(9 * 3);
    }

    public ItemCollection(String collection_id, List<ItemStack> stacks) {
        this.collection_id = collection_id;
        this.stacks = new ArrayList<>(stacks);
    }

    public ItemCollection(String collection_id) {
        this.collection_id = collection_id;
        this.stacks = new ArrayList<>(9 * 3);
    }

    public static final Codec<ItemCollection> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(Codec.STRING.fieldOf("collection_id").forGetter(c -> c.collection_id),
            ItemStack.CODEC.listOf().fieldOf("stacks").orElseGet(() -> new ArrayList<>()).forGetter(c -> c.stacks))
        .apply(instance, ItemCollection::new));
}
