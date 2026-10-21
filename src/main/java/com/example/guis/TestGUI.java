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

        this.setTitle(Component.literal("Reward Menu"));
        this.setupMainMenu();
    }

    private static final int[] BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8 };
    private static final int[] CUSTOM_SLOTS = { 11, 15 };
    public static String[] CUSTOM_COMPONENTS = { "Custom Rewards", "Vanilla Rewards" };
    public static Item[] CUSTOM_MODELS = { Items.SUNFLOWER, Items.GOLDEN_CARROT };

    private void setupMainMenu() {
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

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Make sure the user is not trying to grab the divider

        for (int b: CUSTOM_SLOTS)
            if (index == b){
                TestVanillaGUI g = new TestVanillaGUI(player);
                g.open();
            }

        return true;
    }

    private int selectionSlot(int slot){
        //First slot + Available Row Slots * Row Number
        return 10 + (slot % 7) + (slot / 7) * 9;
    }

    private Slot[] offerings() {
        Slot[] slots = new Slot[12];

        for (int i = 0; i < 12; i++)
            slots[i] = this.getCustomSlot(i % 4 + 9 * (i / 4));

        return slots;

    }

}