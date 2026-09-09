package me.uuun.warProject.data;

import lombok.RequiredArgsConstructor;
import me.uuun.warProject.WarProject;
import me.uuun.warProject.bug.Bugger;
import me.uuun.warProject.model.CityData;
import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Ideology;
import me.uuun.warProject.model.Tile;
import me.uuun.warProject.util.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
public class MapLoader {
    private final WarProject plugin;

    public Set<Country> getCountries(String mapName) {
        Set<Country> countries = new LinkedHashSet<>();

        File mapsFolder = new File(plugin.getDataFolder(), "maps");
        File mapFile = new File(mapsFolder, mapName + ".yml");

        if (!mapFile.exists()) {
            plugin.getLogger().severe("Файл карты не найден: " + mapFile.getPath());
            return countries;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(mapFile);

        ConfigurationSection countriesSection = config.getConfigurationSection("countries");
        if (countriesSection == null) {
            plugin.getLogger().warning("Секция countries не найдена в файле " + mapFile.getName());
            return countries;
        }

        for (String countryName : countriesSection.getKeys(false)) {
            ConfigurationSection tilesSection = countriesSection.getConfigurationSection(countryName + ".tiles");
            if (tilesSection == null) continue;

            String displayColor = countriesSection.getString(countryName + ".color", "white");
            NamedTextColor color = NamedTextColor.NAMES.value(displayColor.toLowerCase());
            if (color == null) {
                plugin.getLogger().warning("Неизвестный цвет '" + displayColor + "' для страны '" + countryName + "'. Используется WHITE.");
                color = NamedTextColor.WHITE;
            }

            String displayName = countriesSection.getString(countryName + ".displayname", "Неизвестно");
            String ideologyName = countriesSection.getString(countryName + ".ideology", "democracy");
            Ideology ideology;
            try {
                ideology = Ideology.valueOf(ideologyName);
            } catch (IllegalArgumentException e) {
                Bugger.bug("Unknown ideology for " + countryName + " in map " + mapName);
                ideology = Ideology.DEMOCRACY;
            }

            Set<Tile> allTiles = new HashSet<>();

            for (String tileKey : tilesSection.getKeys(false)) {
                ConfigurationSection tileSec = tilesSection.getConfigurationSection(tileKey);
                if (tileSec == null) continue;

                int x = tileSec.getInt("x");
                int z = tileSec.getInt("z");
                boolean isCity = tileSec.getBoolean("isCity", false);

                CityData cityData = null;
                if (isCity) {
                    ConfigurationSection citySec = tileSec.getConfigurationSection("cityData");
                    if (citySec != null) {
                        cityData = CityData.builder()
                                .factories(citySec.getInt("factories", 0))
                                .population(citySec.getLong("population", 0))
                                .defense(citySec.getInt("defense", 0))
                                .isCapital(citySec.getBoolean("isCapital", false))
                                .displayName(tileKey)
                                .build();
                    }
                }
                allTiles.add(new Tile(x, z, isCity, cityData));
            }

            Country country = Country.builder()
                    .name(countryName)
                    .tiles(allTiles)
                    .color(color)
                    .displayName(displayName)
                    .ideology(ideology)
                    .build();

            plugin.getGameManager().getResearchManager().fillCountry(country);

            countries.add(country);
        }

        return countries;
    }

    public void loadMapTiles(Set<Country> countries){
        for(Country country : countries){
            for(Tile tile : country.getTiles()){
                int x = tile.getX();
                int z = tile.getZ();
                int y = -61;

                World world = Bukkit.getWorld("world");
                if(world != null){
                    Location loc = new Location(world, x, y, z);
                    world.getBlockAt(loc).setType(Utils.getCountryMaterialByColor(country.getColor()));
                }
            }
        }
    }
}