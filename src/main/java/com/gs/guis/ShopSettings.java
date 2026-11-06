package com.gs.guis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ShopSettings {

    public boolean confirmation;

    public ShopSettings() {
        confirmation = true;
    }

    public ShopSettings(boolean confirmation) {
        this.confirmation = confirmation;
    }

    public static final Codec<ShopSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("confirmation", true).forGetter(s -> s.confirmation)
    ).apply(instance, ShopSettings::new));
}
