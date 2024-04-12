package net.akazukin.mapart.compat.data;

import lombok.Getter;

public class InventorySlot {
    /*public static InventorySlot OFF_HAND = new InventorySlot(-500);

    public static InventorySlot HELMET = new InventorySlot(-1000);
    public static InventorySlot CHEST_PLATE = new InventorySlot(-1001);
    public static InventorySlot LEGGINGS = new InventorySlot(-1002);
    public static InventorySlot BOOTS = new InventorySlot(-1003);
*/
    @Getter
    private final int slot;

    public InventorySlot(final int slot) {
        this.slot = slot;
    }

    /*public int getFormattedSlot() {
        return ComaptManager.getCompat().getSlot(this);
    }*/
}
