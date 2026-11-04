package com.gs.guis;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import com.gs.guis.GUIPresets.Reward;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

interface Confirmable<T> {
    void confirm(boolean b, T confirmedItem);
}

public class ConfirmationGUI<T> extends SimpleGui {

    Reward reward;
    T item;
    Confirmable<T> conf;

    public ConfirmationGUI(ServerPlayer player, Reward reward, Confirmable<T> g, T item){
        super(MenuType.GENERIC_9x3, player, false);
        this.setTitle(Component.literal("Exchange for this item?"));

        this.reward = reward;
        this.conf = g;
        this.item = item;

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
        Component name = Component.literal("§l" + reward.count() + "x §r" + reward.item().getDefaultInstance().getHoverName().getString());
        this.setSlot(ITEM, new GuiElementBuilder(reward.stack()).setName(name));
        this.setSlot(CONFIRM, new GuiElementBuilder(Items.CONCRETE.green()).setName(Component.literal("Confirm")));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        if(index == DENY){
            conf.confirm(false, item);
            return true;
        } else if(index == CONFIRM){
            conf.confirm(true, item);
            return true;
        }

        return false;
    }
}
