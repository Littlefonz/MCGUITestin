package com.gs.item_storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
    private final HashMap<String, ItemSamples> collections = new HashMap<>();
    private final HashMap<String, Sampler> samplers = new HashMap<>();

    public ItemStorage() {
    }

    public ItemStorage(List<ItemSamples> sampleList) {
        for (ItemSamples c : sampleList)
            this.collections.put(c.id(), c);

    }

    public ItemStorage(HashMap<String, ItemSamples> collections) {
        this.collections.putAll(collections);
    }

    public static ItemStorage fromServer(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public Collection<ItemSamples> allSamples() {
        return collections.values();
    }

    public boolean contains(String id) {
        return this.collections.containsKey(id);
    }

    public @Nullable ItemSamples get(String id) {
        return this.collections.get(id);
    }

    public ItemSamples putIfAbsent(String id) {
        if (collections.containsKey(id)) {
            return collections.get(id);
        }
        ItemSamples ic = new ItemSamples(id);
        collections.put(id, ic);
        setDirty();
        return ic;
    }

    private @Nullable ItemSamples drop(String id) {
        ItemSamples s = this.collections.remove(id);
        if (s != null) {
            samplers.remove(id);
            setDirty();

        }
        return s;
    }

    private void push(ItemSamples samples) {
        collections.put(samples.id(), samples);
        samplers.remove(samples.id());
        setDirty();
    }

    /// Sampler stuff

    public @Nullable Sampler sample(String id) {
        if (!samplers.containsKey(id) && collections.containsKey(id))
            samplers.put(id, new Sampler(collections.get(id)));
        return samplers.get(id);
    }

    /// Editor lock stuff
    private final HashMap<String, StorageEditor> editors = new HashMap<>();

    public final class StorageEditor implements AutoCloseable {
        private final String id;
        private final UUID who;
        private boolean closed;

        private StorageEditor(ItemStorage storage, String id, UUID who) {
            this.id = id;
            this.who = who;
            this.closed = false;
        }

        public ItemSamples get() {
            return ItemStorage.this.get(this.id);
        }

        public void push(ItemSamples samples) {
            if (closed || !samples.id().equals(id))
                throw new IllegalStateException();

            ItemStorage.this.push(samples);
        }

        public void drop() {
            if (closed)
                return;
            ItemStorage.this.drop(id);
            close();
        }

        public String getId() {
            return id;
        }

        public UUID getWho() {
            return who;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() {
            if (closed)
                return;
            editors.remove(id, this);
            closed = true;
        }
    }

    public @Nullable StorageEditor tryEdit(String id, UUID who) {
        if (editors.containsKey(id))
            return null;
        StorageEditor editor = new StorageEditor(this, id, who);
        editors.put(id, editor);
        return editor;
    }

    public @Nullable UUID lockedBy(String id) {
        if (editors.get(id) == null)
            return null;
        return editors.get(id).who;
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
        .group(ItemSamples.CODEC.listOf().fieldOf("collections").forGetter(is -> List.copyOf(is.allSamples())))
        .apply(instance, ItemStorage::new));

    public static final SavedDataType<ItemStorage> TYPE = new SavedDataType<>(GSServerMod.id("item_storage"),
        ItemStorage::new,
        CODEC,
        null);
}
