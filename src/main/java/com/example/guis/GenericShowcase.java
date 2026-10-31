package com.example.guis;

import com.example.Items.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class GenericShowcase extends SimpleGui {
    //Displays
    Item[] display;
    int[] displayAmounts;
    Component[] components;

    //UI Locations
    private static final int[] RED_BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8, (9*3), (9*3)+8 };
    private static final int[] GREY_BORDER = { 1, 2, 3, 4, 5, 6, 7, (9*3)+1, (9*3)+2, (9*3)+3, (9*3)+4, (9*3)+5, (9*3)+6, (9*3)+7};
    private static final int BACK_BUTTON_SLOT = (9*3)+3;
    private static final int CLOSE_BUTTON_SLOT = (9*3)+4;

    // GUI Setup
    public GenericShowcase(ServerPlayer player, Item[] display, int[] displayAmounts, Component[] components) {
        super(MenuType.GENERIC_9x4, player, false);

        this.display = display;
        this.displayAmounts = displayAmounts;
        this.components = components;

        this.setTitle(Component.literal("Showcase"));
        this.setupMainMenu();
    }

    public GenericShowcase(ServerPlayer player) {
        super(MenuType.GENERIC_9x4, player, false);

        this.setTitle(Component.literal("Showcase"));
        this.setupMainMenu();
    }

    private void setupMainMenu() {
        //Gray Border
        for (int s : GREY_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());

        //Red Border
        for (int s : RED_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()).hideTooltip());

        //Back Button
        this.setSlot(BACK_BUTTON_SLOT, new GuiElementBuilder(Items.ARROW).setName(Component.literal("Back")));

        //Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));

        //Reward Section
        for (int s = 0; s < display.length; s++) {
            //Fix suffix for 1x items
            String suffix1 = "s";
            if(displayAmounts[s] == 1) suffix1 = "";

            //Display
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.literal(""));
            lore.add(components[s]);
            Component name = Component.literal("§l" + displayAmounts[s] + "x §r" + display[s].getDefaultInstance().getHoverName().getString() + suffix1);
            this.setSlot(displaySlot(s), new GuiElementBuilder(new ItemStack(display[s], displayAmounts[s])).setName(name).setLore(lore));
        }
    }

    private int displaySlot(int slot){
        //First slot + Available Row Slots * Row Number
        return 10 + (slot % 7) + (slot / 7) * 9;
    }
}
