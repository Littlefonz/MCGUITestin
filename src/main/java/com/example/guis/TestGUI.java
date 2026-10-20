package com.example.guis;


import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

public class TestGUI extends SimpleGui {
    public TestGUI(ServerPlayer player) {
        super(MenuType.GENERIC_9x3, player, false);

        // this.setTitle();
        this.setTitle(Component.literal("Raaagh"));

        this.setupBarriers();
    }

    private static int[] BARRIER_SLOTS = { 10, 13, 16 };

    private void setupBarriers() {
        for (int s : BARRIER_SLOTS)
            this.setSlot(s,
                    new GuiElementBuilder(Items.PLAYER_HEAD).setName(Component.empty()));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Make sure the user is not trying to grab the divider
        for (int b: BARRIER_SLOTS)
            if (index == b)
                return false;

        return true;
    }

    private Slot[] offerings() {
        Slot[] slots = new Slot[12];

        for (int i = 0; i < 12; i++)
            slots[i] = this.getCustomSlot(i % 4 + 9 * (i / 4));

        return slots;

    }

}