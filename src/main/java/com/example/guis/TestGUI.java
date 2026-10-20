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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class TestGUI extends SimpleGui {
    public TestGUI(ServerPlayer player) {
        super(MenuType.GENERIC_9x3, player, false);

        // this.setTitle();
        this.setTitle(Component.literal("Reward Options"));

        this.setupBackground();
        this.setupMenu();
    }

    private static int[] BORDER = { 0, 8, 9, 17, 18, 26 };
    private static int[] CUSTOM_SLOTS = { 11, 15 };
    private static String[] CUSTOM_COMPONENTS = { "Custom Rewards", "Vanilla Rewards" };
    private static Item[] CUSTOM_MODELS = { Items.SUNFLOWER, Items.GOLDEN_CARROT };

    private void setupMenu() {
        for (int s = 0; s < 27; s++){
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()));
        }
        for (int s = 0; s < CUSTOM_SLOTS.length; s++) {
            this.setSlot(CUSTOM_SLOTS[s], new GuiElementBuilder(CUSTOM_MODELS[s]).setName(Component.literal(CUSTOM_COMPONENTS[s])));
        }
        for (int s : BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()));
    }

    private void setupBackground() {

    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Make sure the user is not trying to grab the divider
        for (int b: CUSTOM_SLOTS)
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