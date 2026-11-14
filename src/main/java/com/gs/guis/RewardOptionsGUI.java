package com.gs.guis;

import static com.gs.guis.GUIPresets.CUSTOM_EXCHANGES;
import static com.gs.guis.GUIPresets.CUSTOM_REWARDS;
import static com.gs.guis.GUIPresets.VANILLA_REWARDS;

import com.gs.GSServerMod;

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

public class RewardOptionsGUI extends SimpleGui {

    ShopSettings settings;

    public RewardOptionsGUI(ServerPlayer player) {
        super(MenuType.GENERIC_9x4, player, false);

        settings = player.getAttachedOrSet(GSServerMod.SHOP_SETTINGS_ATTACHMENT, new ShopSettings());

        this.setTitle(Component.literal("Reward Menu"));
        this.setupMainMenu();
    }

    private static final int[] BORDER = { 0, 8, (9), (9) + 8, (9 * 2), (9 * 2) + 8, (9 * 3), (9 * 3) + 8 };
    private static final int[] CUSTOM_SLOTS = { 11, 15, 20 };
    private static final int CLOSE_BUTTON_SLOT = (9 * 3) + 4;

    public String[] COMPONENTS = { "§9§lCustom Rewards", "§2§lVanilla Rewards", "§9§lCustom Item Exchange" };
    public Item[] COMPONENT_MODELS = { Items.SUNFLOWER, Items.GOLDEN_CARROT, Items.BUNDLE };

    private void setupMainMenu() {
        // Background Setup
        for (int s = 0; s < this.size; s++) {
            this.setSlot(s,
                new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());
        }

        // Red Border
        for (int s : BORDER)
            this.setSlot(s,
                new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()).hideTooltip());

        // Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));

        // Main Buttons
        for (int s = 0; s < CUSTOM_SLOTS.length; s++) {
            this.setSlot(CUSTOM_SLOTS[s],
                new GuiElementBuilder(COMPONENT_MODELS[s]).setName(Component.literal(COMPONENTS[s])));
        }
    }

    protected void save_and_close() {
        player.setAttached(GSServerMod.SHOP_SETTINGS_ATTACHMENT, settings);
        close();
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        if (index == CLOSE_BUTTON_SLOT)
            save_and_close();

        GenericShopGUI g;
        if (index == CUSTOM_SLOTS[0]) {
            g = new GenericShopGUI(this, player, settings, CUSTOM_REWARDS);
            g.open();
        } else if (index == CUSTOM_SLOTS[1]) {
            g = new GenericShopGUI(this, player, settings, VANILLA_REWARDS);
            g.open();
        } else if (index == CUSTOM_SLOTS[2]) {
            g = new GenericShopGUI(this, player, settings, CUSTOM_EXCHANGES);
            g.open();
        }

        return true;
    }
}
