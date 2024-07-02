package uk.antiperson.autotorch.gui;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiItemStack {

    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> onClick;

    public GuiItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public GuiItemStack(ItemStack itemStack, String name, List<String> list) {
        this.itemStack = itemStack;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + name);
        itemMeta.setLore(list);
        this.itemStack.setItemMeta(itemMeta);
    }

    public GuiItemStack(GuiItemStack itemStack) {
        this(itemStack.getBukkit().clone());
    }

    public GuiItemStack createItemStack(String itemToReplace, String replacement) {
        GuiItemStack guiItemStack1 = new GuiItemStack(this);
        guiItemStack1.setOnClick(getOnClick());
        ItemStack is = guiItemStack1.replaceItem(itemToReplace, replacement);
        guiItemStack1.setItemStack(is);
        return guiItemStack1;
    }

    public ItemStack replaceItem(String itemToReplace, String replacement) {
        ItemStack is = itemStack.clone();
        ItemMeta itemMeta = is.getItemMeta();
        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String atIndex = lore.get(i);
            atIndex = atIndex.replaceAll(itemToReplace, ChatColor.GRAY + replacement);
            lore.set(i, atIndex);
        }
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        return is;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getBukkit() {
        return itemStack;
    }

    public void setOnClick(Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
    }

    public Consumer<InventoryClickEvent> getOnClick() {
        return onClick;
    }
}
