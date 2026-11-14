package com.gs.guis;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.gs.GSServerMod;
import com.gs.ItemTransactions;
import com.gs.item_storage.ItemSamples;
import com.gs.item_storage.ItemSamples.WeightedStack;
import com.gs.item_storage.ItemStorage;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.AnimatedGuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class CollectionView extends SimpleGui {

    private static final MenuType<?> MENU_TYPE = MenuType.GENERIC_9x6;
    private static final int PAGE_X = 8;
    private static final int PAGE_Y = 6;
    private static final int PAGE_CAPACITY = PAGE_X * PAGE_Y;

    private final ItemStorage storage;
    private final ItemSamples samples;
    private final ArrayList<WeightedStack> stacks;
    private int page = 0;
    private int pageMax;
    private double multiplier = 1;

    private static final int MULTIPLIER_SLOT = 9 * 3 - 1;
    private static final int SAVE_SLOT = 9 * 4 - 1;
    private static final int BCK_SLOT = 9 * 5 - 1;
    private static final int FWD_SLOT = 9 * 6 - 1;
    private static final DecimalFormat df = new DecimalFormat("0.###");

    public CollectionView(final ServerPlayer player, final ItemStorage storage, final ItemSamples collection) {
        super(MENU_TYPE, player, false);

        this.storage = storage;
        this.samples = collection;
        this.stacks = new ArrayList<>(this.samples.collection());

        this.pageMax = Math.max((int) Math.ceil(((double) stacks.size()) / PAGE_CAPACITY), 1);
        updateTitle();
        setPageContents();

        for (int i = 8; i < getVirtualSize(); i += 9) {
            this.setSlot(i, GuiElementBuilder.from(Items.STAINED_GLASS_PANE.gray().getDefaultInstance()).hideTooltip());
        }
        updateMultiplierSlot();
        this.setSlot(SAVE_SLOT,
            GuiElementBuilder.from(Items.CONCRETE.green().getDefaultInstance())
                .setName(Component.literal("Save Changes")));
        this.setSlot(BCK_SLOT,
            GuiElementBuilder.from(Items.LADDER.getDefaultInstance()).setName(Component.literal("Previous Page")));
        this.setSlot(FWD_SLOT,
            GuiElementBuilder.from(Items.LADDER.getDefaultInstance()).setName(Component.literal("Next Page")));
    }

    public void updateTitle() {
        setTitle(Component.literal(String.format("Collection: %s (%d/%d)", samples.collection_id, page + 1, pageMax)));
    }

    public void setPageContents() {
        int startIndex = page * PAGE_CAPACITY;
        int count = Math.clamp(stacks.size() - startIndex, 0, PAGE_CAPACITY);

        float max = 0;
        for (WeightedStack s : stacks)
            max += s.w();

        for (int i = 0; i < count; i++) {
            int idx = i + startIndex;
            int x = i % PAGE_X;
            int y = i / PAGE_X;

            WeightedStack ws = stacks.get(idx);
            float ratio = (ws.w() / max);
            this.setSlot(x + y * 9,
                new GuiElementBuilder(ws.s())
                    .addLoreLine(Component.literal(
                        String.format("§7§oWeight: %s (%s%% chance)", df.format(ws.w()), df.format(ratio * 100))))
                    .addLoreLine(Component
                        .literal(String.format("§7§oLMB: +%s, RMB: -%s", df.format(multiplier), df.format(multiplier))))
                    .addLoreLine(Component.literal(String.format("§7§oSHIFT LMB: +%s, RMB: -%s",
                        df.format(10 * multiplier),
                        df.format(10 * multiplier)))));
        }
        if (count < PAGE_CAPACITY) {
            for (int i = count; i < PAGE_CAPACITY; i++) {

                int x = i % PAGE_X;
                int y = i / PAGE_X;
                this.setSlot(x + y * 9, ItemStack.EMPTY);
            }
        }
    }

    private void updateMultiplierSlot() {

        setSlot(MULTIPLIER_SLOT,
            GuiElementBuilder.from(Items.BONE_MEAL.getDefaultInstance())
                .setName(Component.literal(String.format("Clicks x%s", df.format(multiplier))))
                .addLoreLine(Component.literal("§7§oLMB: *10, RMB: /10"))
                .addLoreLine(Component.literal(String.format("§7§oSHIFT LMB: Reset"))));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {

        if (index == SAVE_SLOT) {
            this.save();
            return false;
        }

        if (index == MULTIPLIER_SLOT) {
            if (type == ClickType.MOUSE_LEFT_SHIFT)
                multiplier = 1;
            else if (type == ClickType.MOUSE_LEFT)
                multiplier *= 10;
            else if (type == ClickType.MOUSE_RIGHT)
                multiplier = Math.max(multiplier / 10, 0.001);

            updateMultiplierSlot();
            setPageContents();
            return false;
        }

        if (index == BCK_SLOT) {
            page = Math.max(page - 1, 0);
            updateTitle();
            setPageContents();
            return false;
        }
        if (index == FWD_SLOT) {
            page = Math.min(page + 1, pageMax - 1);
            updateTitle();
            setPageContents();
            return false;
        }

        if (index != 0 && index % 9 == 0)
            return false;

        int startIndex = page * PAGE_CAPACITY;
        int clickedIndexX = index % 9;
        int clickedIndexY = index / 9;
        int collectionIndex = (clickedIndexX + clickedIndexY * PAGE_X) + startIndex;
        boolean inBounds = collectionIndex < stacks.size() && collectionIndex >= 0;
        if (!inBounds) {
            ItemStack c = player.containerMenu.getCarried();
            if (!c.isEmpty()) {
                stacks.add(new WeightedStack(1.0f, c.copy()));
            }
            setPageContents();
            updateTitle();

            return false;
        }

        WeightedStack stack = stacks.get(collectionIndex);
        // Duplicate item into the inventory
        if (action == ContainerInput.CLONE && player.hasInfiniteMaterials())
            player.containerMenu.setCarried(stack.s());

        // Add to selected item weight
        System.out.println("" + index + " " + type + " " + action);
        float factor = switch (type) {
            case ClickType.MOUSE_LEFT_SHIFT -> 10f;
            case ClickType.MOUSE_LEFT -> 1f;
            case ClickType.MOUSE_RIGHT -> -1f;
            case ClickType.MOUSE_RIGHT_SHIFT -> -10f;
            default -> 1f;
        };
        factor *= multiplier;

        float newWeight = Math.max(stack.w() + factor, 0.0f);
        if (!Float.isFinite(newWeight)) {
            newWeight = 0.0f;
        }

        stacks.set(collectionIndex, stack.withWeight(newWeight));

        setPageContents();
        updateTitle();
        return false;
    }

    @Override
    public void onPlayerClose(boolean success) {
        save();
    }

    public void save() {
        for (int i = stacks.size() - 1; i >= 0; i--) {
            WeightedStack s = stacks.get(i);
            if (s.w().equals(0.0f)) {
                ItemTransactions.giveToPlayer(player, player.getInventory(), s.s());
                stacks.remove(i);
            }
        }

        samples.setItems(stacks);
        storage.setDirty();
        stacks.clear();
        stacks.addAll(samples.collection());
        setPageContents();

        GSServerMod.LOGGER.info(String.format("Player %s saved collection \"%s\" with items \"%s\"",
            player.nameAndId().name(),
            samples.collection_id,
            stacks));
    }

    public boolean incollectionBounds(int index) {
        int startIndex = page * PAGE_CAPACITY;
        int clickedIndexX = index % 9;
        int clickedIndexY = index / 9;
        int collectionIndex = (clickedIndexX + clickedIndexY * PAGE_X) + startIndex;
        return collectionIndex < stacks.size();
    }
}
