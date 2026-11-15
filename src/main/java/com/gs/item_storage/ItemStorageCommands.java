package com.gs.item_storage;

import java.util.ArrayList;

import com.gs.guis.CollectionView;
import com.gs.guis.Confirmable;
import com.gs.guis.ConfirmationGUI;
import com.gs.item_storage.ItemSamples.WeightedStack;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemStorageCommands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("gsis")
                // Create a new item collection
                .then(Commands.literal("create")
                    .then(Commands.argument("collection_id", StringArgumentType.word())
                        .executes(ItemStorageCommands::createNewCollection)))
                // Delete a collection with a confirmation
                .then(Commands.literal("delete")
                    .then(Commands.argument("collection_id", StringArgumentType.word())
                        .suggests(new ItemStorage.IdSuggestionProvider())
                        .executes(ItemStorageCommands::deleteCollection)))
                // Open a collection view
                .then(Commands.literal("open")
                    .then(Commands.argument("collection_id", StringArgumentType.word())
                        .suggests(new ItemStorage.IdSuggestionProvider())
                        .executes(ItemStorageCommands::openCollection)))
                // TODO List out all collections
                // TODO Sample from a given section
                // TODO ???
                // Save item in held hand
                .then(Commands.literal("save")
                    .then(Commands.argument("collection_id", StringArgumentType.word())
                        .suggests(new ItemStorage.IdSuggestionProvider())
                        .then(Commands.literal("inv_all").executes(ItemStorageCommands::saveInvFromSource))
                        .then(Commands.argument("slot", SlotArgument.slot())
                            .executes(ItemStorageCommands::saveSlotFromSource))

                        .then(Commands.argument("target_holder", EntityArgument.player())
                            .then(Commands.argument("slot", SlotArgument.slot())
                                .executes(ItemStorageCommands::saveSlotFromTarget))))));
        });
    }

    public static int createNewCollection(CommandContext<CommandSourceStack> ctx) {
        String id = ctx.getArgument("collection_id", String.class);

        ItemStorage storage = ItemStorage.fromServer(ctx.getSource().getServer());

        if (storage.contains(id)) {
            ctx.getSource()
                .sendSuccess(() -> Component.literal(String.format("§4[GSIS] collection \"%s\" already exists!", id)),
                    false);
            ctx.getSource()
                .sendSuccess(
                    () -> Component
                        .literal(String.format("§4[GSIS] Delete it before trying to make one called \"%s\"", id)),
                    false);
            return 0;
        }

        storage.putIfAbsent(id);

        ctx.getSource()
            .sendSuccess(() -> Component.literal(String.format("§2[GSIS] Collection \"%s\" created.", id)), false);

        return 1;
    }

    public static record DeleteConfirmation(ItemStorage storage) implements Confirmable<String> {
        @Override
        public void confirm(boolean b, String confirmedItem) {
            if (!b)
                return;

            storage.drop(confirmedItem);
        }
    }

    public static int deleteCollection(CommandContext<CommandSourceStack> ctx) {
        ItemStorage storage = ItemStorage.fromServer(ctx.getSource().getServer());
        String id = ctx.getArgument("collection_id", String.class);

        if (!storage.contains(id)) {
            ctx.getSource()
                .sendSuccess(() -> Component.literal(String.format("§4[GSIS] collection id \"%s\" not found!", id)),
                    false);
            return 0;
        }

        new ConfirmationGUI<>(ctx.getSource().getPlayer(),
            GuiElementBuilder.from(Items.PAPER.getDefaultInstance()).setName(Component.literal(id)),
            Component.literal(String.format("Are you sure you want to delete \"%s\"?", id)),
            new DeleteConfirmation(storage),
            id).open();

        return 1;
    }

    public static int openCollection(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        ItemStorage storage = ItemStorage.fromServer(ctx.getSource().getServer());
        String id = ctx.getArgument("collection_id", String.class);
        ItemSamples collection;
        if ((collection = storage.get(id)) == null) {
            ctx.getSource()
                .sendSuccess(() -> Component.literal(String.format("§4[GSIS] collection id \"%s\" not found!", id)),
                    false);
            return 0;
        }

        CollectionView cv = new CollectionView(player, storage, collection);
        cv.open();

        return 1;

    }

    public static int saveInvFromSource(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null)
            return 1;

        return saveInv(ctx, player);
    }

    public static int saveSlotFromSource(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null)
            return 1;

        int slot = SlotArgument.getSlot(ctx, "slot");

        return saveSlot(ctx, player, slot);
    }

    public static int saveSlotFromTarget(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "target_holder");
        } catch (CommandSyntaxException e) {
            return 1;
        }

        int slot = SlotArgument.getSlot(ctx, "slot");

        return saveSlot(ctx, player, slot);
    }

    public static int saveSlot(CommandContext<CommandSourceStack> ctx, ServerPlayer player, int slot) {
        ItemStorage storage = ItemStorage.fromServer(ctx.getSource().getServer());
        String id = ctx.getArgument("collection_id", String.class);

        ItemSamples c;
        if ((c = storage.get(id)) == null) {
            ctx.getSource()
                .sendSuccess(() -> Component.literal(String.format("§4[GSIS] collection id \"%s\" not found!", id)),
                    false);
            return 1;
        }

        ItemStack target = player.getSlot(slot).get();

        if (target == null || target.isEmpty()) {
            ctx.getSource()
                .sendSuccess(
                    () -> Component.literal(String.format("§4[GSIS] Invalid! Target %s is null or empty.", target)),
                    false);

            return 1;
        }

        storage.push(c.withAdded(new WeightedStack(1.0f, target.copy())));

        ctx.getSource()
            .sendSuccess(
                () -> Component
                    .literal(String.format("§2[GSIS] Target \"%s\" saved in collection \"%s\".", target, c.id())),
                false);

        return 0;
    }

    public static int saveInv(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        ItemStorage storage = ItemStorage.fromServer(ctx.getSource().getServer());
        String id = ctx.getArgument("collection_id", String.class);

        ItemSamples c;
        if ((c = storage.get(id)) == null) {
            ctx.getSource()
                .sendSuccess(() -> Component.literal(String.format("§4[GSIS] collection id \"%s\" not found!", id)),
                    false);
            return 1;
        }

        Inventory target = player.getInventory();

        if (target == null || target.isEmpty()) {
            ctx.getSource()
                .sendSuccess(
                    () -> Component.literal(String.format("§4[GSIS] Invalid! Target %s is null or empty.", target)),
                    false);

            return 1;
        }

        ArrayList<WeightedStack> stacks = new ArrayList<>();
        int count = 0;
        for (ItemStack stack : target) {
            if (stack.isEmpty())
                continue;

            stacks.add(new WeightedStack(1.0f, stack.copy()));
            count++;
        }

        storage.push(c.withAdded(stacks));

        String s = String.format("§2[GSIS] Saved %d items into collection \"%s\".", count, c.id());

        ctx.getSource().sendSuccess(() -> Component.literal(s), false);

        return 0;
    }
}
