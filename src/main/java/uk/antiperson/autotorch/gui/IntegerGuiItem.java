package uk.antiperson.autotorch.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.antiperson.autotorch.AutoTorch;

import java.util.ArrayList;
import java.util.List;

public class IntegerGuiItem extends GuiItem {

    private int value;
    private final int minValue;
    private final int maxValue;
    private GuiItemStack currentItem;

    public IntegerGuiItem(int minValue, int maxValue, int value) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
    }

    @Override
    public void onClick(InventoryClickEvent consumer) {
        switch (consumer.getClick()) {
            case LEFT:
                add(1);
                break;
            case SHIFT_LEFT:
                add(10);
                break;
            case RIGHT:
                add(-1);
                break;
            case SHIFT_RIGHT:
                add(-10);
                break;
        }
        setUpdated(true);
    }

    public void add(int amount) {
        int result = getValue() + amount;
        if (result <= maxValue && result >= minValue) {
            value = result;
        }
    }

    @Override
    public GuiItemStack getItemStack() {
        ItemStack replaced = super.getItemStack().replaceItem("%size%", getValue() + "");
        if (currentItem != null) {
            currentItem.setItemStack(replaced);
            return currentItem;
        }
        currentItem = new GuiItemStack(replaced);
        return currentItem;
    }

    @Override
    public void setItemStack(GuiItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getBukkit().getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(AutoTorch.DEFAULT_LORE);
        itemMeta.setLore(lore);
        itemStack.getBukkit().setItemMeta(itemMeta);
        super.setItemStack(itemStack);
    }

    public int getValue() {
        return value;
    }
}
