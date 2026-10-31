package com.gs.guis;

import com.gs.Items.ModItems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class GUIPresets {
    //Wheel Item Components
    public static final Item[] WHEEL_ITEMS = { Items.GOLD_BLOCK, Items.DIAMOND, ModItems.PRIZE_TICKET, Items.DIAMOND, ModItems.ASTRO_COIN, ModItems.ENERGY_CUBE, Items.ECHO_SHARD, ModItems.RUSTED_CROWN };
    public static final int[] WHEEL_ITEM_AMOUNTS = { 12, 10, 2, 25, 1, 1, 2, 1 };
    public static final Component[] WHEEL_ITEM_CHANCES = { Component.literal("§724.4% Chance (25 Weight)"), Component.literal("§719.5% Chance (20 Weight)"), Component.literal("§714.6% Chance (15 Weight)"), Component.literal("§714.6% Chance (15 Weight)"), Component.literal("§79.7% Chance (10 Weight)"), Component.literal("§79.7% Chance (10 Weight)"), Component.literal("§75.8% Chance (6 Weight)"), Component.literal("§71.6% Chance (1.6 Weight)")};

    //Voucher Components
    public static final Item[] VOUCHER_ITEMS = { Items.OAK_SIGN, Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER};
    public static final int[] VOUCHER_ITEM_AMOUNTS = { 1, 1, 1, 1, 1, 1 };
    public static final Component[] VOUCHER_ITEM_CHANCES = { Component.literal("§7Chances of §8Common, §9Rare, §5Epic, §6Legendary, §7and §dMythic §7Artifacts"), Component.literal("§745% Chance §8(45 Weight)"), Component.literal("§730% Chance §9(30 Weight)"), Component.literal("§715% Chance §5(15 Weight)"), Component.literal("§79% Chance §6(9 Weight)"), Component.literal("§71% Chance §d(1 Weight)") };

    //Custom GUI Components
    public static final Item[] CUSTOM_REWARDS = { ModItems.WHEEL_TOKEN, ModItems.ARTIFACT_VOUCHER };
    public static final int[] CUSTOM_REWARD_AMOUNT = { 1, 1 };
    public static final int[] CUSTOM_COST = { 1, 5 };

    //Vanilla GUI Components
    public static final Item[] VANILLA_REWARDS = {  Items.STRAW_BED, Items.GOLDEN_APPLE, Items.DIAMOND, Items.COAL_BLOCK, Items.EXPERIENCE_BOTTLE, Items.COOKED_BEEF, Items.FIREWORK_ROCKET, Items.OMINOUS_TRIAL_KEY, Items.TRIAL_KEY, Items.ABANDONED_CAMP_MAP, Items.BURIED_MINESHAFT_MAP, Items.BURIED_TREASURE_MAP, Items.DESERT_PYRAMID_MAP, Items.JUNGLE_PYRAMID_MAP, Items.OCEAN_MONUMENT_MAP, Items.SWAMP_HUT_MAP, Items.WOODLAND_MANSION_MAP, Items.BURIED_TRIAL_CHAMBERS_MAP, Items.BURIED_ANCIENT_CITY_MAP, Items.TOTEM_OF_UNDYING };
    public static final int[] VANILLA_REWARD_AMOUNT = { 8, 8, 12, 16, 24, 32, 64, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    public static final int[] VANILLA_COST = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 3, 3 };
}
