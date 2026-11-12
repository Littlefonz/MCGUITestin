package com.gs.guis;

public interface Confirmable<T> {
    void confirm(boolean b, T confirmedItem);
}
