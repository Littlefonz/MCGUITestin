package com.gs.guis;

import com.gs.Items.ModItems;
import com.gs.Trades.Offer;
import com.gs.Trades.Trade;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;


public class GUIPresets {
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
