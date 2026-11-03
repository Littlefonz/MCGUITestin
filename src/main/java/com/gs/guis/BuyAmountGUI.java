package com.gs.guis;

import com.gs.Items.ModItems;

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

    ItemStack prize;
    ItemStack token;
    GenericShopGUI g;

    //Confirmation Check
    boolean confirmation = true;

    private static final int BACK_BUTTON_SLOT = 21;
    private static final int CLOSE_BUTTON_SLOT = 22;
    private static final int CONFIRM_BUTTON_SLOT = (9*2)+5;

    private static final int[] BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8 };
    private static final int[] BUY_SLOTS = { 11, 12, 13, 14, 15 };

    public BuyAmountGUI(ServerPlayer player, ItemStack prize, ItemStack token, GenericShopGUI g){
        super(MenuType.GENERIC_9x3, player, false);
        this.setTitle(Component.literal("Select Buy Amount"));

        this.prize = prize;
        this.token = token;
        this.g = g;

        setupMainMenu();
    }

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
        int buyAmount = 2;
        for (int s : BUY_SLOTS){
            ArrayList<Component> lore = new ArrayList<>();

            lore.add(Component.literal("§7Cost: " + token.getCount() * buyAmount + " " + ModItems.PRIZE_TICKET.getDefaultInstance().getHoverName().getString() + "s"));
            lore.add(Component.literal(""));
            lore.add(Component.literal("§eClick to Buy!"));
            this.setSlot(s,
                    new GuiElementBuilder(prize.getItem()).setName(Component.literal(buyAmount * prize.getCount() + "x " + prize.getHoverName().getString())).setCount(buyAmount * prize.getCount()).setLore(lore));
            buyAmount *= 2;
        }

        //Confirmation Button
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.literal(""));
        lore.add(Component.literal("§7Click to Toggle Confirmation, which will prevent"));
        lore.add(Component.literal("§7you from buying items until you confirm again!"));

        if(!confirmation){
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lightGray()).setName(Component.literal("§cShop Confirmation (OFF)")).setLore(lore));
        } else {
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lime()).setName(Component.literal("§aShop Confirmation (ON)")).setLore(lore));
        }

        //Back Button
        this.setSlot(BACK_BUTTON_SLOT, new GuiElementBuilder(Items.ARROW).setName(Component.literal("Back")));

        //Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Only accept the Pickup action
        if (action != ContainerInput.PICKUP)
            return false;

        // Closing Menu
        if (index == BACK_BUTTON_SLOT)
            close();

        // Closing Menu
        if (index == CLOSE_BUTTON_SLOT)
            close();

        if (index == CONFIRM_BUTTON_SLOT){
            confirmation = !confirmation;

            //Confirmation Button
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.literal(""));
            lore.add(Component.literal("§7Click to Toggle Confirmation, which will prevent"));
            lore.add(Component.literal("§7you from buying items until you confirm again!"));

            if(!confirmation){
                this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lightGray()).setName(Component.literal("§cShop Confirmation (OFF)")).setLore(lore));
            } else {
                this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lime()).setName(Component.literal("§aShop Confirmation (ON)")).setLore(lore));
            }

            return false;
        }

        ItemStack tempPrize = prize.copy();
        ItemStack tempToken = token.copy();
        int buyAmount = 2;

        for(int s : BUY_SLOTS){

            tempPrize.setCount(tempPrize.getCount() * buyAmount);
            tempToken.setCount(tempToken.getCount() * buyAmount);

            if(confirmation && index == s){
                ConfirmationGUI g = new ConfirmationGUI(player, tempPrize, tempToken, this.g);
                g.open();
                return false;
            }

            if(index == s){
                player.getInventory().add(tempPrize);
                g.removeNItemsFromContainer(player.getInventory(), tempToken.getItem(), tempToken.getCount());
            }
        }

        return true;
    }

}
