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

    private final ItemStack multiplierStack;

    private static final int MULTIPLIER_SLOT = 9 * 3 - 1;
    private static final int SAVE_SLOT = 9 * 4 - 1;
    private static final int BCK_SLOT = 9 * 5 - 1;
    private static final int FWD_SLOT = 9 * 6 - 1;

    public CollectionView(final ServerPlayer player, final ItemStorage storage, final ItemSamples collection) {
        super(MENU_TYPE, player, false);

        this.storage = storage;
        this.samples = collection;
        this.stacks = new ArrayList<>(this.samples.collection());

        this.multiplierStack = new ItemStack(Items.BONE_MEAL, 10);
        multiplierStack.set(DataComponents.DAMAGE, 0); // Just something to set it apart

        this.pageMax = Math.max((int) Math.ceil(((double) stacks.size()) / PAGE_CAPACITY), 1);
        updateTitle();
        setPageContents();

        this.setSlot(MULTIPLIER_SLOT,
            GuiElementBuilder.from(multiplierStack).setName(Component.literal("+10, +100 shift")));
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
                new AnimatedGuiElementBuilder().setInterval(50) // 2.5s
                    .setItem(ws.s().getItem())
                    .setCount(Math.max((int) Math.ceil(64.0 * ratio), 1))
                    .addLoreLine(Component.literal(String.format("§7§oCount: %d", ws.s().getCount())))
                    .addLoreLine(Component.literal(String.format("§7§oWeight: %.1f (%.3f%%)", ws.w(), ratio * 100)))
                    .addLoreLine(Component.literal("§7§oLeft click: +10, Right click: -10"))
                    .saveItemStack()
                    .setItem(ws.s().getItem())
                    .setCount(ws.s().getCount())
                    .addLoreLine(Component.literal(String.format("§7§oCount: %d", ws.s().getCount())))
                    .addLoreLine(Component.literal(String.format("§7§oWeight: %.1f (%.3f%%)", ws.w(), ratio * 100)))
                    .addLoreLine(Component.literal("§7§oLeft click: +10, Right click: -10"))
                    .saveItemStack());
        }
        if (count < PAGE_CAPACITY) {
            for (int i = count; i < PAGE_CAPACITY; i++) {

                int x = i % PAGE_X;
                int y = i / PAGE_X;
                this.setSlot(x + y * 9, new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()));
            }
        }
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, ContainerInput action) {
        if (player.containerMenu.getCarried() == multiplierStack) {
            if (index < 0)
                player.containerMenu.setCarried(ItemStack.EMPTY);
            return false;
        }

        return super.onAnyClick(index, type, action);
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {

        if (index == SAVE_SLOT) {

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

            return false;
        }

        if (index == MULTIPLIER_SLOT) {
            ItemStack carried = player.containerMenu.getCarried();
            if (carried.isEmpty())
                player.containerMenu.setCarried(multiplierStack);
            else if (carried.equals(multiplierStack))
                player.containerMenu.setCarried(ItemStack.EMPTY);

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
            if (!c.isEmpty() && !(c.equals(multiplierStack))) {
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
            case ClickType.MOUSE_LEFT_SHIFT -> 1f;
            case ClickType.MOUSE_LEFT -> 0.1f;
            case ClickType.MOUSE_RIGHT -> -0.1f;
            case ClickType.MOUSE_RIGHT_SHIFT -> -1f;
            default -> 0.1f;
        };
        factor *= (player.containerMenu.getCarried().equals(multiplierStack)) ? 100 : 1;

        float newWeight = Math.max(stack.w() + factor, 0.0f);

        stacks.set(collectionIndex, stack.withWeight(newWeight));

        setPageContents();
        updateTitle();
        return false;
    }

    @Override
    public boolean canPlayerClose() {
        return !(player.containerMenu.getCarried().equals(multiplierStack));
    }

    public boolean incollectionBounds(int index) {
        int startIndex = page * PAGE_CAPACITY;
        int clickedIndexX = index % 9;
        int clickedIndexY = index / 9;
        int collectionIndex = (clickedIndexX + clickedIndexY * PAGE_X) + startIndex;
        return collectionIndex < stacks.size();
    }
}
