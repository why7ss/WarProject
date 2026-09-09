package me.uuun.warProject.paint;

import me.uuun.warProject.WarProject;
import me.uuun.warProject.data.MapSaver;
import me.uuun.warProject.manager.GameManager;
import me.uuun.warProject.model.CityData;
import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Tile;
import me.uuun.warProject.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapPainter implements Listener {

    private static final int SHOW_RADIUS = 64;

    private final WarProject plugin;
    private final GameManager gameManager; // <-- вместо mapName + countriesByName
    private final Map<UUID, PaintSession> sessions = new HashMap<>();

    public record PaintSession(Country country, BukkitTask particleTask) {}

    public MapPainter(WarProject plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Nullable
    public Country getCountry(String name) {
        return gameManager.getCountryByName(name); // всегда актуальный объект из Game
    }

    public boolean isPainting(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public Country getActiveCountry(Player player) {
        PaintSession session = sessions.get(player.getUniqueId());
        return session == null ? null : session.country();
    }

    public void start(Player player, Country country) {
        stop(player);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> showTiles(player, country), 0L, 15L);
        sessions.put(player.getUniqueId(), new PaintSession(country, task));

        player.sendMessage(Component.text("Режим рисования включён для страны: ")
                .append(Component.text(country.getName(), country.getColor()))
                .append(Component.text(". ПКМ по блоку — добавить тайл, ЛКМ — убрать.", NamedTextColor.GRAY)));
    }

    public void stop(Player player) {
        PaintSession session = sessions.remove(player.getUniqueId());
        if (session == null) return;

        session.particleTask().cancel();

        var game = gameManager.getGame();
        if (game != null) {
            MapSaver.saveCountryTiles(plugin, game.getMapName(), session.country());
        }

        player.sendMessage(Component.text("Рисование остановлено, изменения сохранены.", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        stop(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;

        PaintSession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        Action action = event.getAction();
        boolean isRight = action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR;
        boolean isLeft = action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR;
        if (!isRight && !isLeft) return;

        event.setCancelled(true);

        Block target = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (target == null) {
            player.sendActionBar(Component.text("Ты не смотришь на блок в радиусе 6.", NamedTextColor.RED));
            return;
        }

        int x = target.getX();
        int z = target.getZ();
        Country country = session.country();

        if (isRight) {
            addTile(player, country, x, z, false, null);

            target.setType(Utils.getCountryMaterialByColor(country.getColor()));
        } else {
            removeTile(player, country, x, z);

            target.setType(Material.GRASS_BLOCK);
        }
    }

    private void addTile(Player player, Country country, int x, int z, boolean isCity, CityData cityData) {
        country.getTiles().removeIf(t -> t.getX() == x && t.getZ() == z);
        country.getTiles().add(new Tile(x, z, isCity, cityData));

        player.sendActionBar(Component.text("Добавлен тайл (" + x + ", " + z + ")", NamedTextColor.GREEN));
    }

    private void removeTile(Player player, Country country, int x, int z) {
        boolean removed = country.getTiles().removeIf(t -> t.getX() == x && t.getZ() == z);
        if (removed) {
            player.sendActionBar(Component.text("Тайл (" + x + ", " + z + ") убран", NamedTextColor.YELLOW));
        } else {
            player.sendActionBar(Component.text("На этих координатах не было тайла", NamedTextColor.RED));
        }
    }

    private void showTiles(Player player, Country country) {
        World world = player.getWorld();
        Location loc = player.getLocation();

        Color bukkitColor = Color.fromRGB(country.getColor().value());
        Particle.DustOptions dust = new Particle.DustOptions(bukkitColor, 1.2f);

        for (Tile tile : country.getTiles()) {
            if (Math.abs(loc.getX() - tile.getX()) > SHOW_RADIUS) continue;
            if (Math.abs(loc.getZ() - tile.getZ()) > SHOW_RADIUS) continue;

            int y = world.getHighestBlockYAt(tile.getX(), tile.getZ()) + 1;
            Location particleLoc = new Location(world, tile.getX() + 0.5, y + 0.15, tile.getZ() + 0.5);

            player.spawnParticle(Particle.DUST, particleLoc, tile.isCity() ? 3 : 1, 0.1, 0, 0.1, 0, dust);
        }
    }

    /** Добавляет/обновляет тайл-город прямо в позиции игрока в его активной сессии. */
    public boolean createCityAtPlayer(Player player, String displayName, int factories, long population,
                                      int defense, boolean isCapital) {
        PaintSession session = sessions.get(player.getUniqueId());
        if (session == null) return false;

        Location loc = player.getLocation();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        CityData cityData = CityData.builder()
                .displayName(displayName)
                .factories(factories)
                .population(population)
                .defense(defense)
                .isCapital(isCapital)
                .build();

        addTile(player, session.country(), x, z, true, cityData);
        return true;
    }
}