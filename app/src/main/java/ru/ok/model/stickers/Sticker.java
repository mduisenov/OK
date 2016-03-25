package ru.ok.model.stickers;

public final class Sticker {
    public final String code;
    public final int height;
    public final int price;
    public final int width;

    public Sticker(String code, int price, int width, int height) {
        this.code = code;
        this.price = price;
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return "Sticker{code='" + this.code + '\'' + '}';
    }
}
