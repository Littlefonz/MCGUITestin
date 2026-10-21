package com.example.Items;

import com.example.guis.TestGUI;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.sgui.api.gui.SimpleGui;

public class PrizeBox extends Item implements PolymerItem {
    public PrizeBox(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // Open GUI
        if (user instanceof ServerPlayer serverPlayerEntity) {
            SimpleGui g = new TestGUI(serverPlayerEntity);
            g.open();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PLAYER_HEAD;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("itemTooltip.template-mod.prize_box").withStyle(ChatFormatting.GOLD));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}
