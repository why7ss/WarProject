package me.uuun.warProject.manager;

import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Research;
import me.uuun.warProject.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import me.uuun.darkAPI.DarkAPI;

import java.util.*;

public class ResearchManager implements Listener {
    private final Set<Research> researches = new HashSet<>();

    public ResearchManager(){
        add("Infantry-1", 35000);
        add("Infantry-1-damage", 50000);
        add("Infantry-1-defense", 50000);
        add("Infantry-2", 75000);
        add("Infantry-2-damage", 75000);
        add("Infantry-2-defense", 65000);
        add("Infantry-3", 100000);
        add("Infantry-3-defense", 75000);

        add("Anti-Tank 1", 20000);
        add("Anti-Tank 1-defense", 65000);
        add("Anti-Tank 2", 30000);
        add("Anti-Tank 2-defense", 75000);
        add("Anti-Tank 3", 70000);
        add("Anti-Tank 3-defense", 85000);

        add("Tank 1", 45000);
        add("Tank 1-attack", 30000);
        add("Tank 2", 75000);
        add("Tank 2-attack", 45000);
        add("Tank 3", 100000);
        add("Tank 3-attack", 60000);
        add("Tank 4", 125000);
        add("Tank 4-attack", 80000);
    }

    private void add(String name, int cost){
        researches.add(new Research(name, cost));
    }

    public void fillCountry(Country country){
        country.getResearches().clear();

        country.getResearches().addAll(researches);
    }

    private final Map<UUID, Inventory> invs = new HashMap<>();

    public void openResearchGui(Player player){
        Inventory inv = Bukkit.createInventory(null, 54, DarkAPI.parse("<blue>Улучшения"));
        invs.put(player.getUniqueId(), inv);

        for(int i = 0; i < inv.getSize(); i++){
            DarkAPI.createDisplayBackground(inv, i, Material.GRAY_STAINED_GLASS_PANE);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player player)) return;
        if(event.getClickedInventory() == null) return;

        UUID uuid = player.getUniqueId();
        if(!invs.containsKey(uuid)) return;
        if(!invs.get(uuid).equals(event.getClickedInventory())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player player)) return;
        invs.remove(player.getUniqueId(), event.getInventory());
    }
}