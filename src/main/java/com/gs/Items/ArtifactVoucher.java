package com.gs.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import com.gs.guis.GUIPresets;
import com.gs.guis.GenericShowcase;

import static com.gs.guis.GUIPresets.VOUCHER_REWARDS;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.sgui.api.gui.SimpleGui;

public class ArtifactVoucher extends Item implements PolymerItem {
    public ArtifactVoucher(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // Open GUI
        if (user instanceof ServerPlayer serverPlayerEntity) {
            SimpleGui g = new GenericShowcase(serverPlayerEntity, VOUCHER_REWARDS, GUIPresets.VOUCHER_ITEM_CHANCES);
            g.open();
        }

        return InteractionResult.SUCCESS;
    }



    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.CUSHION.lightGray();
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:light_gray_cushion");
    }


    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context,
		HolderLookup.Provider lookup) {
		// Create the base client-sided item stack representation
        ItemStack out = PolymerItemUtils.createItemStack(itemStack, context, lookup);
        out.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        return out;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("item.gs-server-mod.artifact_voucher.info_1").withStyle(ChatFormatting.GRAY));
        textConsumer.accept(Component.translatable("item.gs-server-mod.artifact_voucher.info_2").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}
