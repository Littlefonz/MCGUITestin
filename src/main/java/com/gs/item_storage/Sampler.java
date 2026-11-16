package com.gs.item_storage;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import com.gs.item_storage.ItemSamples.WeightedStack;

import net.minecraft.world.item.ItemStack;

public class Sampler {

    private final NavigableMap<Double, ItemStack> map = new TreeMap<Double, ItemStack>();
    private final Random random;
    private final double total;

    Sampler(ItemSamples samples) {
        this.random = ThreadLocalRandom.current();
        double tot = 0;
        for (WeightedStack s : samples.collection()) {
            tot += s.w();
            map.put(tot, s.s());
        }
        this.total = tot;
    }

    ItemStack next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue().copy();
    }
}
