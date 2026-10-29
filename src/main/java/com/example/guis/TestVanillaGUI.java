package com.example.guis;


import static com.example.guis.TestGUI.CUSTOM_COMPONENTS;
import static com.example.guis.TestGUI.CUSTOM_MODELS;

import com.example.Items.PrizeBox;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class TestVanillaGUI extends SimpleGui {
    //GUI Setup
    public TestVanillaGUI(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);

        this.setTitle(Component.literal("Reward Menu - Vanilla Items"));
        this.setupMainMenu();
    }

    //Borders
    private static final int[] RED_BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8, (9*3), (9*3)+8, (9*4), (9*4)+8, (9*5), (9*5)+8 };
    private static final int[] GREY_BORDER = { 1, 2, 3, 4, 5, 6, 7, (9*5)+1, (9*5)+2, (9*5)+3, (9*5)+4, (9*5)+5, (9*5)+6, (9*5)+7};

    //Rewards
    private static final Item[] REWARDS = { Items.BUNDLE, Items.GOLDEN_APPLE, Items.DIAMOND, Items.STRAW_BED, Items.COOKED_BEEF, Items.FIREWORK_ROCKET, Items.COAL, Items.ABANDONED_CAMP_MAP, Items.BURIED_MINESHAFT_MAP, Items.BURIED_TREASURE_MAP, Items.DESERT_PYRAMID_MAP, Items.JUNGLE_PYRAMID_MAP, Items.OCEAN_MONUMENT_MAP, Items.SWAMP_HUT_MAP, Items.WARM_OCEAN_RUINS_MAP, Items.WOODLAND_MANSION_MAP };
    private static final int[] REWARD_AMOUNT = { 2, 4, 8, 8, 32, 64, 64, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

    int count = 0;

    private void setupMainMenu() {
        //Grey Border
        for (int s : GREY_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()));
        //Red Border
        for (int s : RED_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()));
        //Rewards
        for (int s = 0; s < REWARDS.length; s++) {
            Component name = Component.literal(REWARD_AMOUNT[s] + "x " + REWARDS[s].getDefaultInstance().getHoverName().getString() + "s");

            this.setSlot(rewardSlot(s), new GuiElementBuilder(new ItemStack(REWARDS[s], REWARD_AMOUNT[s])).setName(name));
        }

        //Title
        this.setSlot(4, new GuiElementBuilder(CUSTOM_MODELS[1]).setName(Component.literal(CUSTOM_COMPONENTS[1])));
        //Back Button
        this.setSlot((9*5+3), new GuiElementBuilder(Items.PLAYER_HEAD).setName(Component.literal("Back")));
        //Close Button
        this.setSlot((9*5+4), new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {

        ItemStack token = new ItemStack(Items.PLAYER_HEAD, 1);

        //Reward Exchange
        for (int i = 0; i < REWARDS.length; i++){
            if (index == rewardSlot(i)){

                ItemStack a = new ItemStack(REWARDS[i], REWARD_AMOUNT[i]);
                //ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(StructureTags.ON_OCEAN_MONUMENT_MAPS)).setMapDecoration(MapDecorationTypes.OCEAN_MONUMENT).setSearchRadius(100).setSkipKnownStructures(true).build();

                if(player.getInventory().contains(token)) {
                    player.getInventory().removeItem(player.getInventory().findSlotMatchingItem(token), 1);
                    player.addItem(a);

                    return false;
                }
            }
        }
        //Traversing Back
        if (index == (9*5+3)) {
            TestGUI g = new TestGUI(player);
            g.open();
            return false;
        }
        //Closing Menu
        if (index == (9*5+4))
            close();

        return true;
    }

    private int rewardSlot(int slot){
        //First slot + Available Row Slots * Row Number
        return 10 + (slot % 7) + (slot / 7) * 9;
    }

//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        if (!context.getLevel().isClientSide()) {
//            // Play the sound and specify location, category and who made the sound.
//            // No entity made the sound, so we specify null.
//            context.getLevel().playSound(null, context.getClickedPos(),
//                    SoundEvents.COPPER_PLACE, SoundSource.PLAYERS,
//                    1f, 1f);
//        }
//
//        return super.useOn(context);
//    }
}