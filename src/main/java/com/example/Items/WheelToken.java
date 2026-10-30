package com.example.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;

public class WheelToken extends Item implements PolymerItem {
    public WheelToken(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.SUNFLOWER;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.template-mod.wheel_token").withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}
