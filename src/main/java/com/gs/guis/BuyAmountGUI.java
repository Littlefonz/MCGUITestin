package com.gs.guis;

import com.gs.GSServerMod;
import com.gs.Items.ModItems;
import com.gs.guis.GUIPresets.Trade;

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

public class BuyAmountGUI extends SimpleGui {

    ShopSettings settings;

    Trade origReward;
    GenericShopGUI g;

    private static final int BACK_BUTTON_SLOT = 21;
    private static final int CLOSE_BUTTON_SLOT = 22;
    private static final int CONFIRM_BUTTON_SLOT = (9*2)+5;

    private static final int[] BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8 };
    private static final int[] BUY_SLOTS = { 11, 12, 13, 14, 15 };

    public BuyAmountGUI(ServerPlayer player, ShopSettings settings, Trade r, GenericShopGUI g){
        super(MenuType.GENERIC_9x3, player, false);
        this.setTitle(Component.literal("Select Buy Amount"));

        this.settings = settings;
        this.g = g;
        this.origReward = r;

        setupMainMenu();
    }

    private void updateSettings() {
        //Confirmation Button
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.literal(""));
        lore.add(Component.literal("§7Click to Toggle Confirmation, which will prevent"));
        lore.add(Component.literal("§7you from buying items until you confirm again!"));

        if(settings.confirmation){
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lime()).setName(Component.literal("§aShop Confirmation (ON)")).setLore(lore));
        } else {
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lightGray()).setName(Component.literal("§cShop Confirmation (OFF)")).setLore(lore));
        }
    }

//    private Trade[] getRewards(Trade reward, int budget) {
//        int CAPACITY = 5; // number of possible rewards
//
//        Trade[] rewards = new Trade[5];
//
//        for (int i = 0; i < CAPACITY; i++) {
//            int multiplier = (int) Math.pow(2, i + 1);
//            int newCount = reward.count(i) * multiplier;
//            int newCost = reward.cost(i) * multiplier;
//            if (budget < newCost)
//                break;
//
//            rewards[i] = new Trade(newCount, reward.item(i), newCost, reward.offer(i));
//        }
//        return rewards;
//    }

    private void setupMainMenu() {
        //Background Setup
        for (int s = 0; s < 27; s++){
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());
        }

        // Red Border
        for (int s : BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()).hideTooltip());

        //Buy Slots
        for (int i = 0; i < BUY_SLOTS.length; i++) {
            Trade r = origReward;

            int s = BUY_SLOTS[i];

            ArrayList<Component> lore = r.defaultLore();
            if (r != null) {
                lore.add(Component.literal(""));
                lore.add(Component.literal("§eClick to Buy!"));
                Component name = Component.literal("§l" + r.count(0) + "x §r" + r.item(0).getDefaultInstance().getHoverName().getString());
                this.setSlot(s, new GuiElementBuilder(r.stack(0)).setName(name).setLore(lore));
            } else {
                int cost = origReward.cost(0) * (int) Math.pow(2, i + 1);
                int count = origReward.count(0) * (int) Math.pow(2, i + 1);
                lore.add(Component.literal("§cCost: " + cost + " " + ModItems.PRIZE_TICKET.getDefaultInstance().getHoverName().getString() + "s"));
                lore.add(Component.literal(""));
                lore.add(Component.literal("§4Not enough " + ModItems.PRIZE_TICKET.getDefaultInstance().getHoverName().getString() + "s!"));
                Component name = Component.literal("§l" + count + "x §r" + origReward.item(0).getDefaultInstance().getHoverName().getString());
                ItemStack stack = origReward.stack(0);
                stack.setCount(1);
                this.setSlot(s, new GuiElementBuilder(stack).setName(name).setLore(lore));
            }

        }

        //Back Button
        this.setSlot(BACK_BUTTON_SLOT, new GuiElementBuilder(Items.ARROW).setName(Component.literal("Back")));

        //Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));

        updateSettings();
    }

    protected void save_and_close() {
        player.setAttached(GSServerMod.SHOP_SETTINGS_ATTACHMENT, settings);
        close();
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Only accept the Pickup action
        if (action != ContainerInput.PICKUP)
            return false;

        // Closing Menu
        if (index == BACK_BUTTON_SLOT) {
            g.confirm(false, origReward);
            return false;
        }

        // Closing Menu
        if (index == CLOSE_BUTTON_SLOT)
            save_and_close();

        if (index == CONFIRM_BUTTON_SLOT){
            settings.confirmation = !settings.confirmation;

            //Confirmation Button
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.literal(""));
            lore.add(Component.literal("§7Click to Toggle Confirmation, which will prevent"));
            lore.add(Component.literal("§7you from buying items until you confirm again!"));

            if(!settings.confirmation){
                this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lightGray()).setName(Component.literal("§cShop Confirmation (OFF)")).setLore(lore));
            } else {
                this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lime()).setName(Component.literal("§aShop Confirmation (ON)")).setLore(lore));
            }

            return false;
        }

        for(int i = 0; i < BUY_SLOTS.length; i++){
            int s = BUY_SLOTS[i];

            if (index == s) {
                Trade r = origReward;
                if (r == null) // Selected reward is not available; they do not have enough to purchase
                    break;

                if (settings.confirmation) {
                    new ConfirmationGUI<>(player, r, this.g, r).open();
                    return false;
                } else {
                    g.confirm(true, r);
                }
            }
        }

        return true;
    }
}
