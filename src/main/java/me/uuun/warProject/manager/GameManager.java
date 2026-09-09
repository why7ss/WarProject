package me.uuun.warProject.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.uuun.warProject.bug.Bugger;
import me.uuun.warProject.data.MapLoader;
import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Game;
import me.uuun.warProject.model.Justification;
import me.uuun.warProject.model.Tile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@RequiredArgsConstructor
public class GameManager {
    private final Plugin plugin;
    private final MapLoader mapLoader;
    @Getter private final ResearchManager researchManager;

    @Nullable private Game game;

    public void startGame(boolean restartIfAlreadyGame, String map){
        if(game != null && !restartIfAlreadyGame) {
            Bugger.bug("startGame: игра уже запущена, restartIfAlreadyGame=false — выхожу без перезапуска");
            return;
        }

        Set<Country> countries = mapLoader.getCountries(map);
        if(countries == null || countries.isEmpty()){
            Bugger.bug("Countries in loaded map '" + map + "' is empty");
            return;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                gameTick(game);
            }
        };

        if(game != null){
            BukkitRunnable tickRunnable = game.getTickRunnable();
            if(tickRunnable != null) tickRunnable.cancel();
            game.setTickRunnable(null);
        }

        game = Game.builder()
                .countries(countries)
                .timestampStarted(System.currentTimeMillis())
                .tickRunnable(runnable)
                .build();

        runnable.runTaskTimer(plugin, 60L, 60L);

        mapLoader.loadMapTiles(countries);

        Bugger.info("startGame: игра запущена, стран загружено: " + countries.size());
    }

    @Nullable public Country getPlayerCountry(Player player){
        if(game == null) return null;
        for (Country gameCountry : game.getCountries()) {
            if(gameCountry.getPlayer() != null && gameCountry.getPlayer().getUniqueId().equals(player.getUniqueId())){
                return gameCountry;
            }
        }
        return null;
    }

    @Nullable
    public Game getGame() {
        return game;
    }

    @Nullable
    public Country getCountryByName(String name) {
        if (game == null) return null;
        for (Country country : game.getCountries()) {
            if (country.getName().equalsIgnoreCase(name)) {
                return country;
            }
        }
        return null;
    }

    public void kickPlayerFromCountry(Player player){
        Country country = getPlayerCountry(player);
        if(country == null) {
            Bugger.warn("Country is null for player " + player.getName());
            return;
        }

        if(player.isOnline()){
            player.setHealth(0);

            player.getInventory().clear();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.isOnline() && player.isDead()){
                    player.spigot().respawn();
                }
            }, 40L);
        } else {
            Bugger.warn("Tried to kick offline player, ignoring");
        }

        player.setAllowFlight(false);

        country.setPlayer(null);
    }

    // one times per 3 seconds
    public void gameTick(Game game){
        if(game == null) return;

        processDiplomacyTicks(game);
    }

    public void processDiplomacyTicks(Game game){
        game.getCountries().forEach(country -> {
            country.setPoliticalPower(country.getPoliticalPower() + 2);

            if(!country.getJustifications().isEmpty()){
                for (Justification justification : country.getJustifications()) {
                    justification.setProgress(Math.min(100, justification.getProgress() + 6));
                }
            }
        });
    }

    public void addInGame(Player player, Country country){
        if(country == null) return;
        if(player == null) return;

        if(country.getPlayer() != null) kickPlayerFromCountry(country.getPlayer());
        country.setPlayer(player);

        Tile capital = country.getCapital();
        if(capital != null) {
            player.teleport(new Location(Bukkit.getWorld("world"), capital.getX() + 0.5, -60, capital.getZ() + 0.5));
        }

        HotbarManager.giveItemsToPlayer(player);

        player.setAllowFlight(true);
        player.setFlying(true);
    }
}