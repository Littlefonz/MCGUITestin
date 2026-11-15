package com.gs.item_storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;

public record ItemSamples(String id, List<WeightedStack> stacks) {

    public ItemSamples {
        var sorted = new ArrayList<>(stacks);
        sorted.sort(WeightedStack::compare);
        stacks = List.copyOf(sorted);
    }

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

    public ItemSamples(String collection_id) {
        this(collection_id, List.of());
    }

    public ItemSamples withStacks(List<WeightedStack> s) {
        return new ItemSamples(id, s);
    }

    public ItemSamples withAdded(Collection<WeightedStack> more) {
        var l = new ArrayList<>(stacks);
        l.addAll(more);
        return new ItemSamples(id, l);
    }

    public ItemSamples withAdded(WeightedStack more) {
        var l = new ArrayList<>(stacks);
        l.add(more);
        return new ItemSamples(id, l);
    }

    public double weightSum() {
        return weightSum(stacks);
    }

    public static double weightSum(Collection<WeightedStack> stacks) {
        return stacks.stream().mapToDouble(WeightedStack::w).sum();
    }

    public Collection<WeightedStack> collection() {
        return stacks;
    }

    public static final Codec<ItemSamples> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(Codec.STRING.fieldOf("collection_id").forGetter(c -> c.id),
            WeightedStack.CODEC.listOf().fieldOf("stacks").orElseGet(() -> List.of()).forGetter(c -> c.stacks))
        .apply(instance, ItemSamples::new));
}
