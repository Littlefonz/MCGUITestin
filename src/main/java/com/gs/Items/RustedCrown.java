package com.gs.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jetbrains.annotations.Nullable;

import com.gs.GSUtils;

import java.util.List;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;

public class RustedCrown extends Item implements PolymerItem {
    public RustedCrown(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.GOLDEN_HELMET;
    }

    @Override
    public @Nullable Identifier
        getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:golden_helmet");
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack,
        TooltipFlag tooltipType,
        PacketContext context,
        HolderLookup.Provider lookup) {
        // Create the base client-sided item stack representation
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, context, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        // Add the redstone coast trim and hide the trim tooltip
        out.set(DataComponents.TRIM,
            new ArmorTrim(lookup.lookupOrThrow(Registries.TRIM_MATERIAL).getOrThrow(TrimMaterials.REDSTONE),
                lookup.lookupOrThrow(Registries.TRIM_PATTERN).getOrThrow(TrimPatterns.COAST)));
        out.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.TRIM, true));
        return out;
    }

    @Override
    public void modifyClientTooltip(List<Component> tooltip, ItemStack stack, PacketContext context) {
        tooltip.addAll(GSUtils.infoComponents.apply("rusted_crown"));
    }
}
