package com.gs.item_storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.Nullable;

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
    private final HashMap<String, ItemSamples> collections;

    public Collection<ItemSamples> allSamples() {
        return collections.values();
    }

    public @Nullable ItemSamples drop(String key) {
        ItemSamples s = this.collections.remove(key);
        if (s != null)
            setDirty();
        return s;
    }

    public boolean contains(String key) {
        return this.collections.containsKey(key);
    }

    public @Nullable ItemSamples get(String key) {
        return this.collections.get(key);
    }

    public ItemStorage() {
        collections = new HashMap<>();
    }

    public ItemStorage(List<ItemSamples> collections) {

        HashMap<String, ItemSamples> h = new HashMap<>();
        for (ItemSamples c : collections)
            h.put(c.collection_id, c);

        this.collections = h;
    }

    public ItemStorage(HashMap<String, ItemSamples> collections) {
        this.collections = collections;
    }

    public static ItemStorage fromServer(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public ItemSamples newCollection(String id) {
        ItemSamples ic = new ItemSamples(id);

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

    public static final Codec<ItemStorage> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(ItemSamples.CODEC.listOf()
            .fieldOf("collections")
            .forGetter(is -> new ArrayList<ItemSamples>(is.allSamples())))
        .apply(instance, ItemStorage::new));

    public static final SavedDataType<ItemStorage> TYPE = new SavedDataType<>(GSServerMod.id("item_storage"),
        ItemStorage::new,
        CODEC,
        null);
}
