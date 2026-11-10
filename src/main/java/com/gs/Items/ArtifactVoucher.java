package com.gs.Items;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import com.gs.GSServerMod;
import com.gs.guis.GUIPresets;
import com.gs.guis.GenericShowcaseGUI;
import com.mojang.serialization.Codec;

import static com.gs.guis.GUIPresets.VOUCHER_REWARDS;

import java.util.function.Consumer;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.netty.buffer.ByteBuf;

public class ArtifactVoucher extends Item implements PolymerItem {
    public ArtifactVoucher(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // Open GUI
        if (user instanceof ServerPlayer serverPlayerEntity) {
            SimpleGui g = new GenericShowcaseGUI(serverPlayerEntity, VOUCHER_REWARDS, GUIPresets.VOUCHER_ITEM_CHANCES);
            g.open();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.CUSHION.lightGray();
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent,
            Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(
                Component.translatable("item.gs-server-mod.artifact_voucher.info_1").withStyle(ChatFormatting.GRAY));
        textConsumer.accept(
                Component.translatable("item.gs-server-mod.artifact_voucher.info_2").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }

    public enum RarityComponent implements StringRepresentable, TooltipProvider {
        COMMON(0),
        RARE(1),
        EPIC(2),
        LEGENDARY(3),
        MYTHICAL(4);

        private final int id;

        RarityComponent(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String getSerializedName() {
            switch (this) {
                case COMMON:
                    return "common";
                case RARE:
                    return "rare";
                case EPIC:
                    return "epic";
                case LEGENDARY:
                    return "legendary";
                case MYTHICAL:
                    return "mythical";
            }
            // Unreachable
            return "";
        }

        public String displayName() {
            switch (this) {
                case COMMON:
                    return "§7Common";
                case RARE:
                    return "§cRare";
                case EPIC:
                    return "§dEpic";
                case LEGENDARY:
                    return "§aLegendary";
                case MYTHICAL:
                    return "§bMythical";
            }
            // Unreachable
            return "";

        }

        @Override
        public void addToTooltip(TooltipContext context, Consumer<Component> consumer, TooltipFlag flag,
                DataComponentGetter components) {
            consumer.accept(Component.translatable("item.gs-server-mod.artifact_voucher.rarity", this.displayName()));
        }

        public static final Codec<RarityComponent> CODEC = StringRepresentable.fromEnum(RarityComponent::values);

        // Maps integer IDs back to enum values safely (returns FIRST if out of bounds)
        public static final java.util.function.IntFunction<RarityComponent> BY_ID = ByIdMap
                .continuous(RarityComponent::getId, RarityComponent.values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        // StreamCodec for networking
        public static final StreamCodec<ByteBuf, RarityComponent> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
                RarityComponent::getId);

    }

    public static final DataComponentType<RarityComponent> VOUCHER_RARITY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GSServerMod.id("voucher_rarity"),
            DataComponentType.<RarityComponent>builder().persistent(RarityComponent.CODEC)
                    .build());
}
