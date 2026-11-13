package com.gs.item_storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.gs.GSServerMod;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class ItemStorage extends SavedData {
    private HashMap<String, ItemCollection> collections;

    public HashMap<String, ItemCollection> getCollections() {
        return collections;
    }

    public static Codec<ItemStorage> getCodec() {
        return CODEC;
    }

    public static SavedDataType<ItemStorage> getType() {
        return TYPE;
    }

    public ItemStorage() {
        collections = new HashMap<>();
    }

    public ItemStorage(List<ItemCollection> collections) {

        HashMap<String, ItemCollection> h = new HashMap<>();
        for (ItemCollection c : collections)
            h.put(c.collection_id, c);

        this.collections = h;
    }

    public ItemStorage(HashMap<String, ItemCollection> collections) {
        this.collections = collections;
    }

    public static ItemStorage fromServer(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public ItemCollection newCollection(String id) {
        ItemCollection ic = new ItemCollection(id);

        if (!collections.containsKey(id)) {
            collections.put(id, ic);
            setDirty();
        }

        return ic;
    }

    public static class IdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

            ItemStorage storage = ItemStorage.fromServer(context.getSource().getServer());

            for (String k : storage.collections.keySet().stream().sorted().toList()) {
                builder.suggest(k);
            }

            return builder.buildFuture();
        }
    }

    private static final Codec<ItemStorage> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(ItemCollection.CODEC.listOf()
            .fieldOf("collections")
            .forGetter(is -> new ArrayList<ItemCollection>(is.getCollections().values())))
        .apply(instance, ItemStorage::new));

    private static final SavedDataType<ItemStorage> TYPE = new SavedDataType<>(GSServerMod.id("item_storage"),
        ItemStorage::new,
        CODEC,
        null);
}
