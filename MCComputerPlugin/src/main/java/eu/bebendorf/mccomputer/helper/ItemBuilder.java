package eu.bebendorf.mccomputer.helper;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Arrays;


public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, int data) {
        item = new ItemStack(material, amount, (short) data);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder setData(int data) {
        item.setDurability((short) data);
        return this;
    }

    public ItemBuilder setMaterial(Material m) {
        item.setType(m);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(name);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta m = item.getItemMeta();
        m.setLore(Arrays.asList(lore));
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder enchant(Enchantment ench, int lvl) {
        if (item.getType().getId() == 403) {
            return enchantBook(ench, lvl);
        }
        item.addUnsafeEnchantment(ench, lvl);
        return this;
    }

    public ItemBuilder enchantBook(Enchantment ench, int lvl) {
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) item.getItemMeta();
        m.addStoredEnchant(ench, lvl, false);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flag) {
        ItemMeta m = item.getItemMeta();
        m.addItemFlags(flag);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) return this;
        LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
        m.setColor(color);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return this;
        SkullMeta m = (SkullMeta) item.getItemMeta();
        m.setOwner(owner);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setPotionType(PotionEffectType type) {
        if (!(item.getItemMeta() instanceof PotionMeta)) return this;
        PotionMeta m = (PotionMeta) item.getItemMeta();
        m.setMainEffect(type);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookAuthor(String author) {
        BookMeta m = (BookMeta) item.getItemMeta();
        m.setAuthor(author);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookContent(String... pages) {
        BookMeta m = (BookMeta) item.getItemMeta();
        m.setPages(pages);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookTitle(String title) {
        BookMeta m = (BookMeta) item.getItemMeta();
        m.setTitle(title);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookMeta(String title, String author, String... pages) {
        BookMeta m = (BookMeta) item.getItemMeta();
        m.setTitle(title);
        m.setAuthor(author);
        m.setPages(pages);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder addBookEnchantment(Enchantment ench, int lvl) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(ench, lvl, false);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setEggType(EntityType type) {
        if ((item != null) && (item.getType() == Material.MONSTER_EGG) && (type != null) && (type.getName() != null)) {
            try {
                String version = Bukkit.getServer().getClass().toString().split("\\.")[3];
                Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");

                Object nmsItemStack = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
                Object nbtTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").newInstance();

                Field nbtTagCompoundField = nmsItemStack.getClass().getDeclaredField("tag");
                nbtTagCompoundField.setAccessible(true);

                nbtTagCompound.getClass().getMethod("setString", String.class, String.class).invoke(nbtTagCompound, "id", type.getName());
                nbtTagCompound.getClass().getMethod("set", String.class, Class.forName("net.minecraft.server." + version + ".NBTBase")).invoke(nbtTagCompoundField.get(nmsItemStack), "EntityTag", nbtTagCompound);

                item = (ItemStack) craftItemStack.getDeclaredMethod("asCraftMirror", nmsItemStack.getClass()).invoke(null, nmsItemStack);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta m = item.getItemMeta();
        m.spigot().setUnbreakable(unbreakable);
        item.setItemMeta(m);
        return this;
    }

    public ItemStack build() {
        return item;
    }

    public static int slot(int line, int col) {
        return ((line - 1) * 9) + (col - 1);
    }

    public static ItemBuilder builder(Material material) {
        return builder(material, 1);
    }

    public static ItemBuilder builder(Material material, int amount) {
        return builder(material, amount, 0);
    }

    public static ItemBuilder builder(Material material, int amount, int data) {
        return new ItemBuilder(material, amount, data);
    }

}
