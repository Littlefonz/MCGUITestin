package com.example.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;

public class RustedCrown extends Item implements PolymerItem{
    public RustedCrown(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.GOLDEN_HELMET;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:golden_helmet");
    }


    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context,
		HolderLookup.Provider lookup) {
		// Create the base client-sided item stack representation
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, context, lookup);

        // Add a zero-level enchantment to trigger the visual glint on the client
        out.enchant(lookup.getOrThrow(Enchantments.MENDING), 1);

        return out;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.template-mod.rusted_crown").withStyle(ChatFormatting.GOLD));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}
