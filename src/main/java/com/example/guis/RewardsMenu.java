package com.example.guis;


import com.example.Items.ModItems;

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

    //Custom GUI Components
    private static final Item[] CUSTOM_REWARDS = { ModItems.WHEEL_TOKEN, ModItems.ARTIFACT_VOUCHER };
    private static final int[] CUSTOM_REWARD_AMOUNT = { 1, 1 };
    private static final int[] CUSTOM_COST = { 1, 3 };

    //Vanilla GUI Components
    private static final Item[] VANILLA_REWARDS = { Items.GOLDEN_APPLE, Items.DIAMOND, Items.STRAW_BED, Items.EXPERIENCE_BOTTLE, Items.COOKED_BEEF, Items.FIREWORK_ROCKET, Items.COAL, Items.ABANDONED_CAMP_MAP, Items.BURIED_MINESHAFT_MAP, Items.BURIED_TREASURE_MAP, Items.DESERT_PYRAMID_MAP, Items.JUNGLE_PYRAMID_MAP, Items.OCEAN_MONUMENT_MAP, Items.SWAMP_HUT_MAP, Items.WOODLAND_MANSION_MAP, Items.BURIED_TRIAL_CHAMBERS_MAP, Items.BURIED_ANCIENT_CITY_MAP };
    private static final int[] VANILLA_REWARD_AMOUNT = { 4, 8, 8, 16, 32, 64, 64, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    private static final int[] VANILLA_COST = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2 };

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
            g = new GenericShop(player, CUSTOM_REWARDS, CUSTOM_REWARD_AMOUNT, CUSTOM_COST);
            g.open();
        } else if (index == CUSTOM_SLOTS[1]){
            g = new GenericShop(player, VANILLA_REWARDS, VANILLA_REWARD_AMOUNT, VANILLA_COST);
            g.open();
        }

        return true;
    }
}