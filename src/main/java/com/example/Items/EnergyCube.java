package com.example.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;

public class EnergyCube extends Item implements PolymerItem{
    public EnergyCube(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.BLUE_ICE;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:blue_ice");
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        PolymerItem.super.modifyBasePolymerItemStack(out, stack, context, lookup);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.template-mod.energy_cube").withStyle(ChatFormatting.AQUA));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}
