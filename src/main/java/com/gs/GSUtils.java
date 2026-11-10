package com.gs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GSUtils {
    public static ArrayList<String> getInfoKeys(String item_key) {
        ArrayList<String> lst = new ArrayList<>(10);

        Language lang = Language.getInstance();

        Function<String, String> k = (String s) -> "item." + GSServerMod.MOD_ID + "." + item_key + "." + s;

        String key;
        if (lang.has(key = k.apply("info"))) {
            lst.add(key);
            return lst;
        }

        int x = 1;
        while (lang.has(key = k.apply("info_" + x))) {
            lst.add(key);
            x++;
        }
        return lst;
    }

    public static Function<String, List<MutableComponent>> infoComponents = (String s) -> getInfoKeys(s).stream()
            .map((String k) -> Component.translatable(k)).toList();
}
