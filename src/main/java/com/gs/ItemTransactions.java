package com.gs;

import java.util.Objects;

import com.gs.Trades.Offer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemTransactions {
    public static void giveToPlayer(ServerPlayer player, Inventory inv, ItemStack stack) {
        if (stack.isEmpty())
            return;

        inv.add(stack); // mutates stack to report remaining items
        if (!stack.isEmpty()) { // Stack is always empty after `inv.add()` in creative mode! works properly in
                                // survival.
            player.drop(stack, true, Prediction.SERVER_ONLY);
        }
    }

    public static void removeFromContainer(Container c, Item item, int n) {
        int removedCount, remaining = n;
        int size = c.getContainerSize();
        for (int i = 0; remaining > 0 && i < size; i++) {
            ItemStack stack = Objects.requireNonNull(c.getSlot(i)).get();
            if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, item.getDefaultInstance()))
                continue;

            c.removeItem(i, removedCount = Math.min(stack.count(), remaining));
            remaining -= removedCount;
        }
    }

    public static void removeFromContainer(Container c, Offer o) {
        int removedCount, remaining = o.count();
        int size = c.getContainerSize();
        for (int i = 0; remaining > 0 && i < size; i++) {
            ItemStack stack = Objects.requireNonNull(c.getSlot(i)).get();
            if (!o.isSameItemSameComponents(stack))
                continue;
            c.removeItem(i, removedCount = Math.min(stack.count(), remaining));
            remaining -= removedCount;
        }
    }
}
