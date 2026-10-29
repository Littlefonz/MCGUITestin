package com.example.guis;

import static com.example.guis.TestGUI.CUSTOM_COMPONENTS;
import static com.example.guis.TestGUI.CUSTOM_MODELS;

import java.util.Map;

import com.example.Items.PrizeBox;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
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

public class TestVanillaGUI extends SimpleGui {
    // GUI Setup
    public TestVanillaGUI(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);

        this.setTitle(Component.literal("Reward Menu - Vanilla Items"));
        this.setupMainMenu();
    }

    //UI stuff
    private static final int[] RED_BORDER = { 0, 8, (9), (9)+8, (9*2), (9*2)+8, (9*3), (9*3)+8, (9*4), (9*4)+8, (9*5), (9*5)+8 };
    private static final int[] GREY_BORDER = { 1, 2, 3, 4, 5, 6, 7, (9*5)+1, (9*5)+2, (9*5)+3, (9*5)+4, (9*5)+5, (9*5)+6, (9*5)+7};
    private static final int TITLE_SLOT = 4;
    private static final int BACK_BUTTON_SLOT = (9*5+3);
    private static final int CLOSE_BUTTON_SLOT = (9*5+4);

    //Rewards
    private static final Item[] REWARDS = { Items.BUNDLE, Items.GOLDEN_APPLE, Items.DIAMOND, Items.STRAW_BED, Items.COOKED_BEEF, Items.FIREWORK_ROCKET, Items.COAL, Items.ABANDONED_CAMP_MAP, Items.BURIED_MINESHAFT_MAP, Items.BURIED_TREASURE_MAP, Items.DESERT_PYRAMID_MAP, Items.JUNGLE_PYRAMID_MAP, Items.OCEAN_MONUMENT_MAP, Items.SWAMP_HUT_MAP, Items.WARM_OCEAN_RUINS_MAP, Items.WOODLAND_MANSION_MAP };
    private static final int[] REWARD_AMOUNT = { 2, 4, 8, 8, 32, 64, 64, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

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
                                                    MapDecorationTypes.WOODLAND_MANSION));

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
        this.setSlot(TITLE_SLOT, new GuiElementBuilder(CUSTOM_MODELS[1]).setName(Component.literal(CUSTOM_COMPONENTS[1])));
        //Back Button
        this.setSlot(BACK_BUTTON_SLOT, new GuiElementBuilder(Items.PLAYER_HEAD).setName(Component.literal("Back")));
        //Close Button
        this.setSlot(CLOSE_BUTTON_SLOT, new GuiElementBuilder(Items.BARRIER).setName(Component.literal("Close")));
    }

    @Override
    public boolean onClick(int index, ClickType type, ContainerInput action, GuiElement element) {

        ItemStack token = new ItemStack(Items.PLAYER_HEAD, 1);

        //Reward Exchange
        for (int i = 0; i < REWARDS.length; i++){
            if (index == rewardSlot(i)){

                ItemStack a = itemFromReward(i);

                if(player.getInventory().contains(token)) {
                    player.getInventory().removeItem(player.getInventory().findSlotMatchingItem(token), 1);
                    player.addItem(a);

                    return false;
                }
            }
        }
        //Traversing Back
        if (index == BACK_BUTTON_SLOT) {
            TestGUI g = new TestGUI(player);
            g.open();
            return false;
        }
        //Closing Menu
        if (index == CLOSE_BUTTON_SLOT)
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

    private ItemStack itemFromReward(int slot) {
        // Check if the reward is a map and handle it accordingly
        MapType type = MAP_TYPES.get(REWARDS[slot]);
        if (type != null) {
            ItemStack map = filledMap(type, REWARDS[slot], REWARD_AMOUNT[slot]);
            return map;
        }

        return new ItemStack(REWARDS[slot], REWARD_AMOUNT[slot]);
    }

    private ItemStack filledMap(MapType type, Item originalItem, int amount) {
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