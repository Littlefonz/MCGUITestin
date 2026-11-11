package com.gs.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import com.gs.GSUtils;
import com.gs.guis.GUIPresets;
import com.gs.guis.GenericShowcaseGUI;

import java.util.List;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.sgui.api.gui.SimpleGui;

public class WheelToken extends Item implements PolymerItem {
    public WheelToken(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // Open GUI
        if (user instanceof ServerPlayer serverPlayerEntity) {
            SimpleGui g = new GenericShowcaseGUI(serverPlayerEntity, GUIPresets.WHEEL_REWARDS, GUIPresets.WHEEL_ITEM_CHANCES);
            g.open();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.SUNFLOWER;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:sunflower");
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context,
		Provider lookup) {
		// Create the base client-sided item stack representation
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, context, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        return out;
    }

    @Override
    public void modifyClientTooltip(List<Component> tooltip, ItemStack stack, PacketContext context) {
        tooltip.addAll(GSUtils.infoComponents.apply("wheel_token"));
    }
}
