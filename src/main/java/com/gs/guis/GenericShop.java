package com.gs.guis;

import java.util.ArrayList;
import java.util.Map;

import com.gs.Items.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;

public class GenericShop extends SimpleGui {

    //Rewards
    Item[] rewards;
    int[] rewardAmounts;
    int[] costs;
    String components;
    Item title;

    //UI Locations
    private static final int[] RED_BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8, (9*3), (9*3)+8, (9*4), (9*4)+8, (9*5), (9*5)+8 };
    private static final int[] GREY_BORDER = { 1, 2, 3, 4, 5, 6, 7, (9*5)+1, (9*5)+2, (9*5)+3, (9*5)+4, (9*5)+5, (9*5)+6, (9*5)+7};
    private static final int BACK_BUTTON_SLOT = (9*5)+3;
    private static final int CLOSE_BUTTON_SLOT = (9*5)+4;
    private static final int TITLE_SLOT = 4;

    //Mapping
    private record MapType(TagKey<Structure> tag, Holder<MapDecorationType> decoration) {}
    private static final Map<Item, MapType> MAP_TYPES = Map.of(
            Items.ABANDONED_CAMP_MAP,   new MapType(StructureTags.ON_ABANDONED_CAMP_SWAMP_MAPS, // TODO make this variable!!!
                    MapDecorationTypes.ABANDONED_CAMP),
            Items.BURIED_MINESHAFT_MAP, new MapType(StructureTags.ON_MINESHAFT_MAPS,
                    MapDecorationTypes.MINESHAFT),
            Items.BURIED_TREASURE_MAP,  new MapType(StructureTags.ON_TREASURE_MAPS,
                    MapDecorationTypes.RED_X),
            Items.DESERT_PYRAMID_MAP,   new MapType(StructureTags.ON_DESERT_PYRAMID_MAPS,
                    MapDecorationTypes.DESERT_PYRAMID),
            Items.JUNGLE_PYRAMID_MAP,   new MapType(StructureTags.ON_JUNGLE_PYRAMID_MAPS,
                    MapDecorationTypes.JUNGLE_TEMPLE),
            Items.OCEAN_MONUMENT_MAP,   new MapType(StructureTags.ON_OCEAN_MONUMENT_MAPS,
                    MapDecorationTypes.OCEAN_MONUMENT),
            Items.SWAMP_HUT_MAP,        new MapType(StructureTags.ON_SWAMP_HUT_MAPS,
                    MapDecorationTypes.SWAMP_HUT),
            Items.WOODLAND_MANSION_MAP, new MapType(StructureTags.ON_WOODLAND_MANSION_MAPS,
                    MapDecorationTypes.WOODLAND_MANSION),
            Items.BURIED_TRIAL_CHAMBERS_MAP, new MapType(StructureTags.ON_BURIED_TRIAL_CHAMBERS_MAPS,
                    MapDecorationTypes.TRIAL_CHAMBERS),
            Items.BURIED_ANCIENT_CITY_MAP, new MapType(StructureTags.ON_ANCIENT_CITY_MAPS,
                    MapDecorationTypes.ANCIENT_CITY));

    // GUI Setup
    public GenericShop(ServerPlayer player, Item[] rewards, int[] rewardAmounts, int[] costs, Item title, String components) {
        super(MenuType.GENERIC_9x6, player, false);

        this.rewards = rewards;
        this.rewardAmounts = rewardAmounts;
        this.costs = costs;
        this.components = components;
        this.title = title;

        this.setTitle(Component.literal("Reward Menu"));
        this.setupMainMenu();
    }

    private void setupMainMenu() {
        //Reward Section
        for (int s = 0; s < rewards.length; s++) {
            //Fix suffix for 1x items
            String suffix = "s";
            if(costs[s] == 1) suffix = "";

            //Display
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.literal(""));
            lore.add(Component.literal("§7Cost: " + costs[s] + " " + ModItems.PRIZE_TICKET.getDefaultInstance().getHoverName().getString() + suffix));
            lore.add(Component.literal("§eClick to Exchange!"));
            Component name = Component.literal("§l" + rewardAmounts[s] + "x §r" + rewards[s].getDefaultInstance().getHoverName().getString());
            this.setSlot(rewardSlot(s), new GuiElementBuilder(new ItemStack(rewards[s], rewardAmounts[s])).setName(name).setLore(lore));
        }

        //Gray Border
        for (int s : GREY_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.gray()).setName(Component.empty()).hideTooltip());

        //Red Border
        for (int s : RED_BORDER)
            this.setSlot(s,
                    new GuiElementBuilder(Items.STAINED_GLASS_PANE.red()).setName(Component.empty()).hideTooltip());

        //Title
        this.setSlot(TITLE_SLOT, new GuiElementBuilder(title).setName(Component.literal(components)));

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

        // Traversing Back
        if (index == BACK_BUTTON_SLOT) {
            RewardsMenu g = new RewardsMenu(player);
            g.open();
            return false;
        }

        // Closing Menu
        if (index == CLOSE_BUTTON_SLOT)
            close();

        // Reward selection check
        int slot = -1;
        // Find & check if the player selected a reward slot
        for (int i = 0; i < rewards.length; i++){
            if (index == rewardSlot(i)) {
                slot = i;
                break;
            }
        }
        if (slot == -1)
            return false;

        // REWARD SECTION
        Inventory inv = player.getInventory();

        // Player currency count
        int ticketCount = inv.countItem(ModItems.PRIZE_TICKET);
        if (ticketCount <= 0) // How did we get here...?
            return false;

        // Check if the player has enough currency for the given reward
        if (slot == -1 || ticketCount < costs[slot])
            return false;

        // Process the transaction
        ItemStack reward = processReward(slot);
        // Check if the player has space, or if removing the tickets would give space for the item
        int stacksize = 64;
        int unfinishedStackCnt = inv.countItem(reward.getItem()) % stacksize;

        if (
            inv.getFreeSlot() != -1 // Slots exist
            || (ticketCount - costs[slot] % stacksize == 0) // Removing the tickets would give space
            || (unfinishedStackCnt > 0 && ((stacksize - unfinishedStackCnt) >= reward.count())) // There exists a viable stack for this item
        ) {
            // Add to inventory
            removeNItemsFromContainer(inv, ModItems.PRIZE_TICKET, costs[slot]);
            inv.add(reward);
        } else {
            // Drop on the ground
            removeNItemsFromContainer(inv, ModItems.PRIZE_TICKET, costs[slot]);
            player.drop(reward, true, Prediction.SERVER_ONLY);
        }
        return false;
    }

    private int removeNItemsFromContainer(Container c, Item item, int n) {
        int remaining = n;
        for (int i = 0; remaining > 0 && i < c.getContainerSize(); i++) {
            ItemStack slot = c.getSlot(i).get();
            if (slot.isEmpty() || slot.getItem() != item)
                continue;

            int removedCount = Math.min(slot.count(), remaining);
            c.removeItem(i, removedCount);
            remaining = Math.max(remaining - removedCount, 0);
        }

        // `remaining` SHOULD be 0.
        return remaining;
    }

    private int rewardSlot(int slot){
        //First slot + Available Row Slots * Row Number
        return 10 + (slot % 7) + (slot / 7) * 9;
    }

    private ItemStack processReward(int slot) {
        // Check if the reward is a map and handle it accordingly
        MapType type = MAP_TYPES.get(rewards[slot]);
        if (type != null) {
            return filledMap(type, rewards[slot], rewardAmounts[slot]);
        }

        // Else return the reward
        return new ItemStack(rewards[slot], rewardAmounts[slot]);
    }

    private ItemStack filledMap(MapType type, Item originalItem, int amount) {
        // Maps
        ServerLevel level = player.level();

        BlockPos pos = level.findNearestMapStructure(type.tag(), player.blockPosition(), 100, true);

        if (pos == null) {
            System.out.println("No structure found nearby for this map");
            return null;
        }

        ItemStack map = originalItem.getDefaultInstance();
        map.setCount(amount);

        MapItem.applyNewSavedData(level, map, pos.getX(), pos.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, pos, "+", type.decoration());

        return map;
    }
}
