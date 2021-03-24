package net.chunkhopper.src.utils;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.nbt.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    public static ItemStack getSkullFromBase64(String base64)
    {
        ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        notNull(base64, "base64");

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    public static ItemStack getSkullFromName(String name) {
        ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        notNull(name, "name");

        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:\"" + name + "\"}"
        );
    }

    private static void notNull(Object o, String name) {
        if (o == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }

    public static ItemStack getItemStack(Material material, int amount, short data)
    {
        return new ItemStack(material, amount, data);
    }

    public static ItemStack getItemStack(Material material, int amount, short data, String name)
    {
        ItemStack itemStack = new ItemStack(material, amount, data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatUtils.chat(name));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(Material material, int amount, short data, String name, List<String> lore)
    {
        ItemStack itemStack = new ItemStack(material, amount, data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<String> lore)
    {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name)
    {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, String NBTname, int value)
    {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        NBT nbt = NBT.get(itemStack);
        nbt.setInt(NBTname, value);
        return nbt.apply(itemStack);
    }

    public static ItemStack getHopperStack(int amount, int level, int invSize) {
        NBT nbt;

        List<String> lore = Arrays.asList(ChatUtils.chat("&fThis hopper will collect all items that"),
                ChatUtils.chat("&fdrop in its chunk!"),
                "",
                ChatColor.WHITE + "It also stores " + ChatColor.AQUA + "54 stacks",
                ChatColor.WHITE + "and transfers " + ChatColor.AQUA + (level*9) + " stacks per second");

        ItemStack retrohopper = ItemBuilder.getItemStack(Material.HOPPER, amount, (short) 0, Main.name, lore);

        nbt = NBT.get(retrohopper);
        nbt.setBoolean("retrohopper", true);
        nbt.setInt("Level", level);
        nbt.setInt("invSize", invSize);

        return nbt.apply(retrohopper);
    }



    public static boolean isRetrohopper(ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() ||
                !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore() || NBT.get(item) == null
        || !NBT.get(item).getString("retrohopper").equals(String.valueOf(true)))
            return false;
        return true;
    }

}
