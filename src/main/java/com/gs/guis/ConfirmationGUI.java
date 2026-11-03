package com.gs.guis;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class ConfirmationGUI extends SimpleGui {

    ItemStack prize;
    ItemStack token;
    GenericShopGUI g;

    public ConfirmationGUI(ServerPlayer player, ItemStack prize, ItemStack token, GenericShopGUI g){
        super(MenuType.GENERIC_9x3, player, false);
        this.setTitle(Component.literal("Exchange for this item?"));

        this.prize = prize;
        this.token = token;
        this.g = g;

        setupMainMenu();
    }

    private static final int DENY = 11;
    private static final int ITEM = 13;
    private static final int CONFIRM = 15;

    private void setupMainMenu() {
        // Background Setup
        for (int s = 0; s < 27; s++){
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());
        }

        this.setSlot(DENY, new GuiElementBuilder(Items.CONCRETE.red()).setName(Component.literal("Deny")));
        this.setSlot(ITEM, new GuiElementBuilder(prize).setName(Component.literal(prize.getCount() + "x " + prize.getHoverName().getString())).setCount(prize.getCount()));
        this.setSlot(CONFIRM, new GuiElementBuilder(Items.CONCRETE.green()).setName(Component.literal("Confirm")));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        if(index == DENY){
            g.open();
            return true;
        } else if(index == CONFIRM){
            player.getInventory().add(prize);
            g.removeNItemsFromContainer(player.getInventory(), token.getItem(), token.count());
            g.open();
            return true;
        }

        return false;
    }
}
