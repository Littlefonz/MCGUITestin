package com.gs.guis;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.gs.item_storage.ItemCollection;
import com.gs.item_storage.ItemStorage;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class CollectionView extends SimpleGui {

    private static final MenuType<?> MENU_TYPE = MenuType.GENERIC_9x6;
    private static final int PAGE_X = 8;
    private static final int PAGE_Y = 6;
    private static final int PAGE_CAPACITY = PAGE_X * PAGE_Y;

    private final ItemStorage storage;
    private final ItemCollection collection;
    private int page = 0;
    private int pageMax;

    private static final int TRASH_SLOT = 9 * 4 - 1;
    private static final int BCK_SLOT = 9 * 5 - 1;
    private static final int FWD_SLOT = 9 * 6 - 1;

    public CollectionView(final ServerPlayer player, final ItemStorage storage, final ItemCollection collection) {
        super(MENU_TYPE, player, false);

        this.storage = storage;
        this.collection = collection;
        this.pageMax = Math.max((int) Math.ceil(((double) collection.stacks.size()) / PAGE_CAPACITY), 1);
        updateTitle();
        setPageContents();

        this.setSlot(TRASH_SLOT,
            GuiElementBuilder.from(Items.CAULDRON.getDefaultInstance()).setName(Component.literal("Trash Item")));
        this.setSlot(BCK_SLOT,
            GuiElementBuilder.from(Items.LADDER.getDefaultInstance()).setName(Component.literal("Previous Page")));
        this.setSlot(FWD_SLOT,
            GuiElementBuilder.from(Items.LADDER.getDefaultInstance()).setName(Component.literal("Next Page")));
    }

    public void updateTitle() {
        setTitle(
            Component.literal(String.format("Collection: %s (%d/%d)", collection.collection_id, page + 1, pageMax)));
    }

    public void setPageContents() {
        setPageContents(0);
    }

    public void setPageContents(int startIdx) {
        int startIndex = page * PAGE_CAPACITY;
        int count = Math.min(collection.stacks.size() - startIndex, PAGE_CAPACITY);

        for (int i = 0; i < count; i++) {
            int idx = i + startIndex;
            int x = i % PAGE_X;
            int y = i / PAGE_X;

            if (x + y * 9 < startIdx)
                continue;
            this.setSlot(x + y * 9, new GuiElementBuilder(collection.stacks.get(idx)));
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
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {

        if (index == TRASH_SLOT) {
            // TODO
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

        System.out.println("" + index + " " + type + " " + action + " " + element);
        int startIndex = page * PAGE_CAPACITY;
        int clickedIndexX = index % 9;
        int clickedIndexY = index / 9;
        int collectionIndex = (clickedIndexX + clickedIndexY * PAGE_X) + startIndex;
        boolean inBounds = collectionIndex < collection.stacks.size();

        System.out.println(collectionIndex);
        System.out.println(player.containerMenu.getCarried());
        ItemStack carriedItem = player.containerMenu.getCarried();

        if (carriedItem.isEmpty()) {
            if (inBounds) {
                // Take the item from the collection and give it to the player
                ItemStack s = collection.stacks.get(collectionIndex);
                if (action != ContainerInput.CLONE)
                    collection.stacks.remove(collectionIndex);
                player.containerMenu.setCarried(s);
            }
        } else {
            // Place the carried item into the collection
            if (inBounds)
                collection.stacks.add(collectionIndex, carriedItem);
            else
                collection.stacks.addLast(carriedItem);

            if (action != ContainerInput.CLONE)
                player.containerMenu.setCarried(ItemStack.EMPTY);
        }

        this.pageMax = Math.max((int) Math.ceil(((double) collection.stacks.size()) / PAGE_CAPACITY), 1);
        this.page = Math.min(pageMax - 1, page);
        if (inBounds)
            setPageContents(collectionIndex);
        else
            setPageContents();


        storage.setDirty();
        updateTitle();
        return true;
    }
}
