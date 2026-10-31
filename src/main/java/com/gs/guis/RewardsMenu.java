package com.gs.guis;


import static com.gs.guis.GUIPresets.CUSTOM_REWARDS;
import static com.gs.guis.GUIPresets.VANILLA_REWARDS;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RewardsMenu extends SimpleGui {

    public RewardsMenu(ServerPlayer player) {
        super(MenuType.GENERIC_9x3, player, false);

        this.setTitle(Component.literal("Reward Menu"));
        this.setupMainMenu();
    }

    private static final int[] BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8 };
    private static final int[] CUSTOM_SLOTS = { 11, 15 };
    private static final int CLOSE_BUTTON_SLOT = (9*2)+4;

    public static String[] COMPONENTS = { "§9§lCustom Rewards", "§2§lVanilla Rewards" };
    public static Item[] COMPONENT_MODELS = { Items.SUNFLOWER, Items.GOLDEN_CARROT };

    private void setupMainMenu() {
        // Background Setup
        for (int s = 0; s < 27; s++){
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());
        }

        // Red Border
        for (int s : BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()).hideTooltip());

        //Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));

        // Main Buttons
        for (int s = 0; s < CUSTOM_SLOTS.length; s++) {
            this.setSlot(CUSTOM_SLOTS[s], new GuiElementBuilder(COMPONENT_MODELS[s]).setName(Component.literal(COMPONENTS[s])));
        }
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Make sure the user is not trying to grab the divider
        GenericShop g;
        if (index == CUSTOM_SLOTS[0]){
            g = new GenericShop(player, CUSTOM_REWARDS, COMPONENT_MODELS[0], COMPONENTS[0]);
            g.open();
        } else if (index == CUSTOM_SLOTS[1]){
            g = new GenericShop(player, VANILLA_REWARDS, COMPONENT_MODELS[1], COMPONENTS[1]);
            g.open();
        }

        return true;
    }
}
