package com.gs.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import com.gs.GSUtils;

import java.util.List;
import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;



public class AspectOfTheVoid extends Item implements PolymerItem {
    public AspectOfTheVoid(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {

        int COST = 1;
        int MAX_TP_DISTANCE = 8;
        boolean TP_TO_BLOCK = false;

        if(user.isShiftKeyDown()){
            COST = 4;
            MAX_TP_DISTANCE = 61;
            TP_TO_BLOCK = true;
        }

        // Open GUI
        if (user instanceof ServerPlayer serverUser && level instanceof ServerLevel serverLevel) {

            Inventory playerInventory = user.getInventory();
            ServerLevel playerDimension = level.getServer().getLevel(level.dimension());

            if(playerInventory.countItem(Items.ENDER_PEARL) > COST){
                // Initial Values
                double HORIZONTAL_TP_OFFSET = 0.5;
                int VERTICAL_TP_OFFSET = 1;
                int finalTPDistance = 0;
                Vec3 finalTPLocation = null;
                Vec3 finalTPMomentum = new Vec3(0,0,0);

                Vec3 nextTPLocation;
                Block TPBlockLocation;
                boolean validTP;
                boolean validSpecialTP;

                // Check if the block the player is looking at is a valid TP location
                do{
                    // Player Crosshair to World Location (Player Looking Direction (* Y Direction Offset) * TP Distance + Player Position +- Offsets)
                    double crosshairX = -Math.sin(Math.toRadians(user.getYRot())) * Math.cos(Math.toRadians(user.getXRot())) * finalTPDistance + user.getX() - HORIZONTAL_TP_OFFSET;
                    double crosshairY = -Math.sin(Math.toRadians(user.getXRot())) * finalTPDistance + user.getY() + VERTICAL_TP_OFFSET;
                    double crosshairZ = Math.cos(Math.toRadians(user.getYRot())) * Math.cos(Math.toRadians(user.getXRot())) * finalTPDistance + user.getZ() - HORIZONTAL_TP_OFFSET;

                    // Set the TP Block Location
                    int blockXPos = (int) Math.round(crosshairX);
                    int blockYPos = (int) Math.round(crosshairY);
                    int blockZPos = (int) Math.round(crosshairZ);
                    TPBlockLocation = serverLevel.getBlockState(new BlockPos(blockXPos, blockYPos, blockZPos)).getBlock();

                    // Set the selected location to the next TP location
                    nextTPLocation = new Vec3(blockXPos + HORIZONTAL_TP_OFFSET, blockYPos, blockZPos + HORIZONTAL_TP_OFFSET);

                    // Verify then set the next TP location
                    validTP = TPBlockLocation == Blocks.AIR || TPBlockLocation == Blocks.CAVE_AIR || TPBlockLocation == Blocks.VOID_AIR || TPBlockLocation == Blocks.WATER;
                    if(validTP){
                        finalTPLocation = nextTPLocation;
                        finalTPDistance++;
                    }

                    // Verify the special TP location
                    Block blockAboveTP = serverLevel.getBlockState(new BlockPos(blockXPos, blockYPos + 1, blockZPos)).getBlock();
                    validSpecialTP = !validTP && (blockAboveTP == Blocks.AIR || blockAboveTP == Blocks.CAVE_AIR || blockAboveTP == Blocks.VOID_AIR || blockAboveTP == Blocks.WATER);

                } while(validTP && finalTPDistance < MAX_TP_DISTANCE);

                // Ability Checks
                if((validSpecialTP || !TP_TO_BLOCK) && finalTPDistance > 0){
                    playerInventory.removeItem(new ItemStack(Items.ENDER_PEARL, COST));

                    user.teleport(new TeleportTransition(playerDimension, finalTPLocation, finalTPMomentum, serverUser.getYRot(), serverUser.getXRot(), TeleportTransition.DO_NOTHING));
                    user.resetFallDistance();

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.DIAMOND_SWORD;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.parse("minecraft:diamond_spear");
    }

    @Override
    public void modifyClientTooltip(List<Component> tooltip, ItemStack stack, PacketContext context) {
        tooltip.addAll(GSUtils.infoComponents.apply("aspect_of_the_void"));
    }
}
