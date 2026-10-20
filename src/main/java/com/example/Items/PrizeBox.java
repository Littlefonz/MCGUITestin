package com.example.Items;

import com.example.guis.TestGUI;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import eu.pb4.sgui.api.gui.SimpleGui;

public class PrizeBox extends Item {
    public PrizeBox(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // This is to prevent desync.
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        // Open GUI
        SimpleGui g = new TestGUI();
        g.open();

        return InteractionResult.SUCCESS;
    }
}
