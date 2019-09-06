package net.retrohopper.src.serializable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class SerializableItemStack implements Serializable {
    private int amount;
    private int typeID;
    private byte data;
    private short durability;
    private String displayName;
    private Map<Enchantment, Integer> enchantmentList;
    private List<String> lore;

    public SerializableItemStack(ItemStack itemStack) {
        this.amount = itemStack.getAmount();
        this.durability = itemStack.getDurability();
        this.enchantmentList = itemStack.getEnchantments();
        this.typeID = itemStack.getTypeId();
        this.data = itemStack.getData().getData();
        this.displayName = itemStack.getItemMeta().getDisplayName();
        this.lore = itemStack.getItemMeta().getLore();
    }

    public ItemStack toItemStack() {
        ItemStack newStack = new ItemStack(this.typeID, this.amount);
        newStack.setData(new MaterialData(this.typeID, this.data));
        newStack.setDurability(this.durability);
        newStack.addEnchantments(this.enchantmentList);
        ItemMeta newMeta = newStack.getItemMeta();
        newMeta.setLore(this.lore);
        newMeta.setDisplayName(this.displayName);
        newStack.setItemMeta(newMeta);
        return newStack;
    }
}

