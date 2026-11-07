package com.gs;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Trades {

    public Trades() {
	}

	public record Offer(int count, Item item) {}

    public record Reward(int count, Item item) {}

    public record Trade(Offer[] offers, Reward[] rewards) {
        public Trade() {
            this(null, null);
        }

        public static Trade get(int count, Item item) {
            return new Trade(null, new Reward[] { new Reward(count, item) });
        }

        public static Trade get(Reward[] rewards) {
            return new Trade(null, rewards);
        }

        public Trade from(int price, Item item) {
            return new Trade(new Offer[] { new Offer(price, item) }, rewards);
        }

        public Trade from(Offer[] offerings) {
            return new Trade(offerings, rewards);
        }

        public ItemStack stack(int index) {
            return new ItemStack(rewards[index].item, rewards[index].count);
        }

        public Item item(int index) {
            return rewards[index].item;
        }

        public int count(int index) {
            return rewards[index].count;
        }

        public Item offer(int index) {
            return offers[index].item;
        }

        public int cost(int index) {
            return offers[index].count;
        }

        public String getString() {
            StringBuilder offerstr = new StringBuilder();
            StringBuilder rewardstr = new StringBuilder();
            if (offers != null) for (Offer offer : offers) {
                offerstr.append(
                    String.format(
                        "%sx%d, ",
                        offer.item
                            .getDefaultInstance()
                            .getHoverName()
                            .getString(),
                        offer.count
                    )
                );
            }
            if (rewards != null) for (Reward reward : rewards) {
                rewardstr.append(
                    String.format(
                        "%sx%d, ",
                        reward.item
                            .getDefaultInstance()
                            .getHoverName()
                            .getString(),
                        reward.count
                    )
                );
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
                newOffers[i] = new Offer(
                    offers[i].count * multiplier,
                    offers[i].item
                );
            }
            for (int i = 0; i < rewards.length; i++) {
                newRewards[i] = new Reward(
                    rewards[i].count * multiplier,
                    rewards[i].item
                );
            }

            return new Trade(newOffers, newRewards);
        }

        public ArrayList<Component> defaultLore() {
            return defaultLore(1);
        }

        public ArrayList<Component> defaultLore(int multiplier) {
            ArrayList<Component> lore = new ArrayList<>();

            lore.add(Component.empty());
            lore.add(Component.literal("Cost:"));

            for (Offer offer : offers)
                lore.add(
                    Component.literal(
                        "§l" +
                            offer.count * multiplier +
                            "x §r" +
                            offer.item
                                .getDefaultInstance()
                                .getHoverName()
                                .getString()
                    )
                );

            return lore;
        }

        public Component title(int multiplier) {
            StringBuilder title = new StringBuilder();
            for (Reward reward : rewards)
                title
                    .append("§l")
                    .append(reward.count * multiplier)
                    .append("x §r")
                    .append(
                        reward.item
                            .getDefaultInstance()
                            .getHoverName()
                            .getString()
                    )
                    .append(", ");

            return Component.literal(title.substring(0, title.length() - 2));
        }

        public GuiElementBuilder guiElement() {
            return guiElement(1);
        }

        public GuiElementBuilder guiElement(int multiplier) {
            return new GuiElementBuilder(stack(0))
                .setCount(rewards[0].count * Math.max(multiplier, 1))
                .setName(title(multiplier))
                .setLore(defaultLore(multiplier));
        }
    }
}
