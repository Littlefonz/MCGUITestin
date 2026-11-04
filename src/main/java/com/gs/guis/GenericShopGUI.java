package com.gs.guis;

import java.util.ArrayList;
import java.util.Map;

import com.gs.GSServerMod;
import com.gs.Items.ModItems;
import com.gs.guis.GUIPresets.Reward;

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

public class GenericShopGUI extends SimpleGui implements com.gs.guis.Confirmable<Reward> {

    // Shop state
    SimpleGui parent;
    ShopSettings settings;

    //Rewards
    Reward[] rewards;
    String components;
    Item title;

    //UI Locations
    private static final int[] RED_BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8, (9*3), (9*3)+8, (9*4), (9*4)+8, (9*5), (9*5)+8 };
    private static final int[] GREY_BORDER = { 1, 2, 3, 4, 5, 6, 7, (9*5)+1, (9*5)+2, (9*5)+3, (9*5)+4, (9*5)+5, (9*5)+6, (9*5)+7};
    private static final int BACK_BUTTON_SLOT = (9*5)+3;
    private static final int CLOSE_BUTTON_SLOT = (9*5)+4;
    private static final int CONFIRM_BUTTON_SLOT = (9*5)+5;
    private static final int TITLE_SLOT = 4;

    //Mapping
    private record MapType(TagKey<Structure> tag, Holder<MapDecorationType> decoration) {}
    private static final Map<Item, MapType> MAP_TYPES = Map.of(
            Items.ABANDONED_CAMP_MAP,   new MapType(StructureTags.ABANDONED_CAMP,
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
    public GenericShopGUI(SimpleGui parent, ServerPlayer player, ShopSettings settings, Reward[] rewards, Item title, String components) {
        super(MenuType.GENERIC_9x6, player, false);

        this.parent = parent;

        this.settings = settings;
        this.rewards = rewards;
        this.components = components;
        this.title = title;

        this.setTitle(Component.literal("Reward Menu"));
        this.setupMainMenu();
    }

    private void setupMainMenu() {
        //Reward Section
        for (int s = 0; s < rewards.length; s++) {
            Reward r = rewards[s];
            //Fix suffix for 1x items
            String suffix = "s";
            if(rewards[s].cost() == 1) suffix = "";

            //Display
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.literal("§7Cost: " + r.cost() + " " + ModItems.PRIZE_TICKET.getDefaultInstance().getHoverName().getString() + suffix));
            lore.add(Component.literal(""));
            lore.add(Component.literal("§eLeft Click to Exchange!"));
            lore.add(Component.literal("§eRight Click to Bulk Exchange!"));
            Component name = Component.literal("§l" + r.count() + "x §r" + r.item().getDefaultInstance().getHoverName().getString());
            this.setSlot(rewardSlot(s), new GuiElementBuilder(r.stack()).setName(name).setLore(lore));
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

        updateSettings();
    }

    public void updateSettings() {
        //Confirmation Button
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.literal(""));
        lore.add(Component.literal("§7Click to Toggle Confirmation, which will prevent"));
        lore.add(Component.literal("§7you from buying items until you confirm again!"));

        if (settings.confirmation) {
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lime()).setName(Component.literal("§aShop Confirmation (ON)")).setLore(lore));
        } else {
            this.setSlot(CONFIRM_BUTTON_SLOT, new GuiElementBuilder(Items.DYE.lightGray()).setName(Component.literal("§cShop Confirmation (OFF)")).setLore(lore));
        }
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {
        // Only accept the Pickup action
        if (action != ContainerInput.PICKUP)
            return false;

        // Traversing Back
        if (index == BACK_BUTTON_SLOT) {
            parent.open();
            return false;
        }

        // Closing Menu
        if (index == CLOSE_BUTTON_SLOT)
            close();

        if (index == CONFIRM_BUTTON_SLOT){
            this.settings.confirmation = !this.settings.confirmation;
            updateSettings();
            return false;
        }

        // Reward selection
        int slot = -1;
        // Find & check if the player selected a reward slot
        for (int i = 0; i < rewards.length; i++)
            if (index == rewardSlot(i)) {
                slot = i;
                break;
            }
        if (slot != -1) {
            rewardPreCheck(slot, type);
            return false;
        }

        return false;
    }

    public void rewardPreCheck(int slot, ClickType type) {
        Reward reward = rewards[slot];
        Inventory inv = player.getInventory();

        // Player currency count
        int ticketCount = inv.countItem(ModItems.PRIZE_TICKET);
        if (ticketCount <= 0) // How did we get here...?
            return;

        // Check if the player has enough currency for the given reward
        if (ticketCount < reward.cost())
            return;

        if(type.isRight){
            new BuyAmountGUI(player, settings, reward, ticketCount, this).open();
            return;
        }

        if(settings.confirmation){
            new ConfirmationGUI<>(player, reward, this, reward).open();
            return;
        }

        // Otherwise continue as usual
        this.confirm(true, reward);
    }

    public void confirm(boolean confirmed, Reward item) {
        if (!isOpen()) {
            updateSettings();
            open();
        }
        if (!confirmed)
            return;

        // Process the transaction
        processReward(player, item);

    }

    private static void giveToPlayer(ServerPlayer player, Inventory inv, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (inv.getFreeSlot() == -1 && inv.getSlotWithRemainingSpace(stack) == -1) {
            player.drop(stack, true, Prediction.SERVER_ONLY);
            return;
        }

        inv.add(stack); // mutates stack to report remaining items
        if (!stack.isEmpty()) { // Stack is always empty after `inv.add()` in creative mode! works properly in survival.
            giveToPlayer(player, inv, stack);
        }
    }

    public int removeNItemsFromContainer(Container c, Item item, int n) {
        int remaining = n;
        int size = c.getContainerSize();
        for (int i = 0; remaining > 0 && i < size; i++) {
            ItemStack stack = c.getSlot(i).get();
            if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, item.getDefaultInstance()))
                continue;

            int removedCount = Math.min(stack.count(), remaining);
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

    private void processReward(ServerPlayer player, Reward reward) {
        Inventory inv = player.getInventory();
        MapType type = MAP_TYPES.get(reward.item());
        if (type != null) {
            processMap(inv, type, reward);
            return;
        }

        processSimpleReward(inv, reward);
    }

    private void processMap(Inventory inv, MapType type, Reward reward) {
        int i = 0;
        int count = reward.count();
        int cost = 0;
        while (i < count) {
            ItemStack map = fillMap(type, reward.item());
            if (map == null) break; // map type was not found around the player

            // Careful! possible unintended flooring?
            // SHOULD be usable with maps and simple things as the ratio is 1-to-1
            int partialCost = reward.cost() / reward.count();
            cost += partialCost;
            removeNItemsFromContainer(player.getInventory(), reward.offer(), partialCost);
            giveToPlayer(player, inv, map);
            i++;
        }
        GSServerMod.LOGGER.info(String.format(
            "Traded with %s %s",
            player.nameAndId().name(),
            new Reward(i, reward.item(), cost, reward.offer()).getString()
        ));
    }

    private void processSimpleReward(Inventory inv, Reward reward) {
        removeNItemsFromContainer(player.getInventory(), reward.offer(), reward.cost());
        giveToPlayer(player, inv, reward.stack());
        GSServerMod.LOGGER.info(String.format(
            "Traded with %s %s",
            player.nameAndId().name(),
            reward.getString()
        ));
    }

    private ItemStack fillMap(MapType type, Item originalItem) {
        // Maps
        ServerLevel level = player.level();

        BlockPos pos = level.findNearestMapStructure(type.tag(), player.blockPosition(), 100, true);

        if (pos == null) {
            GSServerMod.LOGGER.error("No structure found nearby for this map!");
            player.sendSystemMessage(Component.literal("No structure found nearby for this map!"));
            return null;
        }
        GSServerMod.LOGGER.debug("Generated structure map of type ", type);

        ItemStack map = originalItem.getDefaultInstance();

        MapItem.applyNewSavedData(level, map, pos.getX(), pos.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, pos, "+", type.decoration());

        return map;
    }
}
