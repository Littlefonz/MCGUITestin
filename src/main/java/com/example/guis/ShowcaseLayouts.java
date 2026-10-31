package com.example.guis;

import com.example.Items.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ShowcaseLayouts {
    public static final Item[] WHEEL_ITEMS = { Items.GOLD_BLOCK, Items.DIAMOND, ModItems.WHEEL_TOKEN, Items.DIAMOND, ModItems.ASTRO_COIN, ModItems.ENERGY_CUBE, Items.ECHO_SHARD, ModItems.CROWN };
    public static final int[] WHEEL_ITEM_AMOUNTS = { 12, 10, 1, 25, 1, 1, 2, 1 };
    public static final Component[] WHEEL_ITEM_CHANCES = { Component.literal("§724.4% Chance (25 Weight)"), Component.literal("§719.5% Chance (20 Weight)"), Component.literal("§714.6% Chance (15 Weight)"), Component.literal("§714.6% Chance (15 Weight)"), Component.literal("§79.7% Chance (10 Weight)"), Component.literal("§79.7% Chance (10 Weight)"), Component.literal("§75.8% Chance (6 Weight)"), Component.literal("§71.6% Chance (1.6 Weight)")};

    public static final Item[] VOUCHER_ITEMS = { Items.OAK_SIGN, Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER};
    public static final int[] VOUCHER_ITEM_AMOUNTS = { 1, 1, 1, 1, 1, 1 };
    public static final Component[] VOUCHER_ITEM_CHANCES = { Component.literal("§7Chances of §8Common, §9Rare, §5Epic, §6Legendary, §7and §dMythic §7Artifacts"), Component.literal("§745% Chance §8(45 Weight)"), Component.literal("§730% Chance §9(30 Weight)"), Component.literal("§715% Chance §5(15 Weight)"), Component.literal("§79% Chance §6(9 Weight)"), Component.literal("§71% Chance §d(1 Weight)") };
}
