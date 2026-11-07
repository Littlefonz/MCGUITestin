package com.gs.guis;

import com.gs.Items.ModItems;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

public class GUIPresets {

    public record Offer(int count, Item item) {
    }

    public record Reward(int count, Item item) {
    }

    public record Trade(Offer[] offers, Reward[] rewards) {
        public Trade() {
            this(null, null);
        }

        public static Trade get(int count, Item item) {
            return new Trade(null, new Reward[]{new Reward(count, item)});
        }
        public static Trade get(Reward[] rewards) {
            return new Trade(null, rewards);
        }

        public Trade from(int price, Item item){
            return new Trade(new Offer[]{new Offer(price, item)}, rewards);
        }
        public Trade from(Offer[] offerings){
            return new Trade(offerings, rewards);
        }

        public ItemStack stack(int index) {
            return new ItemStack(rewards[index].item, rewards[index].count);
        }

        public Item item(int index) { return rewards[index].item; }
        public int count(int index) { return rewards[index].count; }
        public Item offer(int index) { return offers[index].item; }
        public int cost(int index) { return offers[index].count; }

        public String getString() {
            StringBuilder offerstr = new StringBuilder();
            StringBuilder rewardstr = new StringBuilder();
            if (offers != null) for (Offer offer : offers) {
                offerstr.append(offer.item.getDefaultInstance().getHoverName().getString()).append("x").append(offer.count).append(", ");
            }
            if (rewards != null) for (Reward reward : rewards) {
                rewardstr.append(reward.item.getDefaultInstance().getHoverName().getString()).append("x").append(reward.count).append(", ");
            }

            return String.format(
                "[%s] -> [%s]",
                offerstr.substring(0, Math.max(offerstr.length() - 2, 0)),
                rewardstr.substring(0, Math.max(rewardstr.length() - 2, 0))
            );
        }

        public int getAffordableCount(ServerPlayer player) {
            Inventory inv = player.getInventory();
            int[] userCount = new int[offers.length];
            for (int i = 0; i < userCount.length; i++) {
                userCount[i] = inv.countItem(offers[i].item);
            }

            int multiplier = 0;
            boolean affordable = true;
            while (affordable) {
                for (int i = 0; i < offers.length; i++) {
                    if (userCount[i] < offers[i].count * multiplier) {
                        affordable = false;
                        break;
                    }
                }
                if (affordable) multiplier++;
            }
            return multiplier;
        }

        public Trade multiply(int multiplier) {
            Offer[] newOffers = new Offer[offers.length];
            Reward[] newRewards = new Reward[rewards.length];

            for (int i = 0; i < offers.length; i++) {
                newOffers[i] = new Offer(offers[i].count * multiplier, offers[i].item);
            }
            for (int i = 0; i < rewards.length; i++) {
                newRewards[i] = new Reward(rewards[i].count * multiplier, rewards[i].item);
            }

            return new Trade(newOffers, newRewards);
        }

        public ArrayList<Component> defaultLore() { return defaultLore(0); }


        public ArrayList<Component> defaultLore(int multiplier) {
            ArrayList<Component> lore = new ArrayList<>();

            if (rewards.length != 1)
                for (Reward reward : rewards)
                    lore.add(Component.literal("§l" + reward.count * multiplier + "x §r" + reward.item.getDefaultInstance().getHoverName().getString()));

            lore.add(Component.empty());
            lore.add(Component.literal("Cost:"));

            for (Offer offer : offers)
                lore.add(Component.literal("§l" + offer.count * multiplier + "x §r" + offer.item.getDefaultInstance().getHoverName().getString()));

            return lore;
        }

        public Component title(int multiplier) {
            StringBuilder title = new StringBuilder();
            for (Reward reward : rewards)
                title
                    .append("§l")
                    .append(reward.count * multiplier)
                    .append("x §r")
                    .append(reward.item.getDefaultInstance().getHoverName().getString())
                    .append(", ");

            return Component.literal(title.substring(0, title.length() - 2));
        }

        public GuiElementBuilder guiElement() { return guiElement(1); }
        public GuiElementBuilder guiElement(int multiplier) {
            return new GuiElementBuilder(stack(0))
                .setCount(rewards[0].count * Math.max(multiplier, 1))
                .setName(title(multiplier))
                .setLore(defaultLore(multiplier));
        }
    }

    //Wheel Item Components
    public static final Trade[] WHEEL_REWARDS = {
        Trade.get(12, Items.GOLD_BLOCK),
        Trade.get(10, Items.DIAMOND),
        Trade.get(2, ModItems.PRIZE_TICKET),
        Trade.get(25, Items.DIAMOND),
        Trade.get(1, ModItems.ASTRO_COIN),
        Trade.get(1, ModItems.ENERGY_CUBE),
        Trade.get(2, Items.ECHO_SHARD),
        Trade.get(1, ModItems.RUSTED_CROWN),
    };
    public static final Component[] WHEEL_ITEM_CHANCES = {
        Component.literal("§724.4% Chance (25 Weight)"),
        Component.literal("§719.5% Chance (20 Weight)"),
        Component.literal("§714.6% Chance (15 Weight)"),
        Component.literal("§714.6% Chance (15 Weight)"),
        Component.literal("§79.7% Chance (10 Weight)"),
        Component.literal("§79.7% Chance (10 Weight)"),
        Component.literal("§75.8% Chance (6 Weight)"),
        Component.literal("§71.6% Chance (1.6 Weight)"),
    };

    //Voucher Components
    public static final Trade[] VOUCHER_REWARDS = {
        Trade.get(1, Items.OAK_SIGN),
        Trade.get(1, Items.PAPER),
        Trade.get(1, Items.PAPER),
        Trade.get(1, Items.PAPER),
        Trade.get(1, Items.PAPER),
        Trade.get(1, Items.PAPER),
    };
    public static final Component[] VOUCHER_ITEM_CHANCES = {
        Component.literal(
            "§7Chances of §8Common, §9Rare, §5Epic, §6Legendary, §7and §dMythic §7Artifacts"
        ),
        Component.literal("§745% Chance §8(45 Weight)"),
        Component.literal("§730% Chance §9(30 Weight)"),
        Component.literal("§715% Chance §5(15 Weight)"),
        Component.literal("§79% Chance §6(9 Weight)"),
        Component.literal("§71% Chance §d(1 Weight)"),
    };

    //Custom GUI Components
    public static final Trade[] CUSTOM_REWARDS = {
        Trade.get(1, ModItems.WHEEL_TOKEN).from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, ModItems.ARTIFACT_VOUCHER).from(5, ModItems.PRIZE_TICKET),
        Trade.get(1, ModItems.ASPECT_OF_THE_VOID).from(new Offer[]{new Offer(1, Items.DIAMOND_SWORD), new Offer(512, Items.ENDER_EYE)})
    };

    //Vanilla GUI Components
    public static final Trade[] VANILLA_REWARDS = {
        Trade.get(8, Items.STRAW_BED)                .from(1, ModItems.PRIZE_TICKET),
        Trade.get(8, Items.GOLDEN_APPLE)             .from(1, ModItems.PRIZE_TICKET),
        Trade.get(12, Items.DIAMOND)                 .from(1, ModItems.PRIZE_TICKET),
        Trade.get(16, Items.COAL_BLOCK)              .from(1, ModItems.PRIZE_TICKET),
        Trade.get(24, Items.EXPERIENCE_BOTTLE)       .from(1, ModItems.PRIZE_TICKET),
        Trade.get(32, Items.COOKED_BEEF)             .from(1, ModItems.PRIZE_TICKET),
        Trade.get(64, Items.FIREWORK_ROCKET)         .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.OMINOUS_TRIAL_KEY)        .from(1, ModItems.PRIZE_TICKET),
        Trade.get(3, Items.TRIAL_KEY)                .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.ABANDONED_CAMP_MAP)       .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.BURIED_MINESHAFT_MAP)     .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.BURIED_TREASURE_MAP)      .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.DESERT_PYRAMID_MAP)       .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.JUNGLE_PYRAMID_MAP)       .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.OCEAN_MONUMENT_MAP)       .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.SWAMP_HUT_MAP)            .from(1, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.WOODLAND_MANSION_MAP)     .from(2, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.BURIED_TRIAL_CHAMBERS_MAP).from(3, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.BURIED_ANCIENT_CITY_MAP)  .from(3, ModItems.PRIZE_TICKET),
        Trade.get(1, Items.TOTEM_OF_UNDYING)         .from(3, ModItems.PRIZE_TICKET)
    };
}
