package me.uuun.warProject.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Utils {
    public static ItemStack createBg(Inventory inv, int slot, Material material){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.setHideTooltip(true);
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
        return item;
    }

    private static final Map<NamedTextColor, Material> COLOR_MATERIALS = Map.ofEntries(
            Map.entry(NamedTextColor.BLACK, Material.BLACK_CONCRETE),
            Map.entry(NamedTextColor.DARK_BLUE, Material.BLUE_CONCRETE),
            Map.entry(NamedTextColor.DARK_GREEN, Material.GREEN_CONCRETE),
            Map.entry(NamedTextColor.DARK_AQUA, Material.CYAN_CONCRETE),
            Map.entry(NamedTextColor.DARK_RED, Material.RED_CONCRETE),
            Map.entry(NamedTextColor.DARK_PURPLE, Material.PURPLE_CONCRETE),
            Map.entry(NamedTextColor.GOLD, Material.ORANGE_CONCRETE),
            Map.entry(NamedTextColor.GRAY, Material.LIGHT_GRAY_CONCRETE),
            Map.entry(NamedTextColor.DARK_GRAY, Material.GRAY_CONCRETE),
            Map.entry(NamedTextColor.BLUE, Material.BLUE_CONCRETE),
            Map.entry(NamedTextColor.GREEN, Material.LIME_CONCRETE),
            Map.entry(NamedTextColor.AQUA, Material.CYAN_CONCRETE),
            Map.entry(NamedTextColor.RED, Material.RED_CONCRETE),
            Map.entry(NamedTextColor.LIGHT_PURPLE, Material.PINK_CONCRETE),
            Map.entry(NamedTextColor.YELLOW, Material.YELLOW_CONCRETE),
            Map.entry(NamedTextColor.WHITE, Material.WHITE_CONCRETE)
    );

    public static Material getCountryMaterialByColor(NamedTextColor color) {
        return COLOR_MATERIALS.getOrDefault(color, Material.WHITE_CONCRETE);
    }
}