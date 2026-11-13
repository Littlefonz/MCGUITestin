package com.gs.item_storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public class ItemSamples {
    public static record WeightedStack(Float w, ItemStack s) {
        public static int compare(WeightedStack x, WeightedStack y) {
            return Float.compare(y.w, x.w);
        }

        public WeightedStack withWeight(float w) {
            return new WeightedStack(w, s);
        }

        public WeightedStack withStack(ItemStack s) {
            return new WeightedStack(w, s);
        }

        public static final Codec<WeightedStack> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.FLOAT.fieldOf("f").forGetter(ws -> ws.w), ItemStack.CODEC.fieldOf("s").forGetter(ws -> ws.s))
            .apply(instance, WeightedStack::new));
    }

    public final String collection_id;
    private final ArrayList<WeightedStack> arr;
    private Double sum = null;

    public ItemSamples() {
        this.collection_id = UUID.randomUUID().toString();
        arr = new ArrayList<>();

    }

    public ItemSamples(String collection_id, List<WeightedStack> stacks) {
        this.collection_id = collection_id;
        arr = new ArrayList<>(Math.max(stacks.size(), 1));
        arr.addAll(stacks);
        arr.sort(WeightedStack::compare);
    }

    public ItemSamples(String collection_id) {
        this.collection_id = collection_id;
        arr = new ArrayList<>();
    }

    public void push(float w, ItemStack s) {
        WeightedStack ws = new WeightedStack(w, s);
        int idx = Collections.binarySearch(arr, ws, WeightedStack::compare);
        if (idx < 0) {
            idx = ~idx; // Equivalent to -(index + 1)
        }
        arr.add(idx, ws);
        this.sum += w;
    }

    public void pop(WeightedStack s) {
        if (arr.contains(s)) {
            arr.remove(s);
            sum = null;
        }
    }

    public int size() {
        return arr.size();
    }

    public double weightSum() {
        if (this.sum != null)
            return sum;

        double sum = 0;
        for (WeightedStack s : arr)
            sum += s.w();
        this.sum = sum;
        return sum;
    }

    public void setItems(List<WeightedStack> q) {
        arr.clear();
        arr.addAll(q);
        arr.sort(WeightedStack::compare);
    }

    public Collection<WeightedStack> collection() {
        return arr;
    }

    public static final Codec<ItemSamples> CODEC = RecordCodecBuilder.create(
        instance -> instance
            .group(Codec.STRING.fieldOf("collection_id").forGetter(c -> c.collection_id),
                WeightedStack.CODEC.listOf()
                    .fieldOf("stacks")
                    .orElseGet(() -> new ArrayList<>())
                    .forGetter(c -> c.arr.stream().toList()))
            .apply(instance, ItemSamples::new));
}
