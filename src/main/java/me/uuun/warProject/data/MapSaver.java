package me.uuun.warProject.data;

import me.uuun.warProject.WarProject;
import me.uuun.warProject.bug.Bugger;
import me.uuun.warProject.model.CityData;
import me.uuun.warProject.model.Country;
import me.uuun.warProject.model.Tile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MapSaver {

    public static void saveCountryTiles(WarProject plugin, String mapName, Country country) {
        File mapsFolder = new File(plugin.getDataFolder(), "maps");
        File mapFile = new File(mapsFolder, mapName + ".yml");

        if (!mapFile.exists()) {
            Bugger.bug("Попытка сохранить тайлы в несуществующий файл карты: " + mapFile.getPath());
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(mapFile);

        String basePath = "countries." + country.getName();
        config.set(basePath + ".color", config.getString(basePath + ".color", "white"));
        config.set(basePath + ".tiles", null); // чистим старые тайлы перед перезаписью

        int index = 0;
        for (Tile tile : country.getTiles()) {
            String key = (tile.isCity() && tile.getCityData() != null && tile.getCityData().getDisplayName() != null)
                    ? tile.getCityData().getDisplayName()
                    : "tile_" + index++;

            String tilePath = basePath + ".tiles." + key;
            config.set(tilePath + ".x", tile.getX());
            config.set(tilePath + ".z", tile.getZ());
            config.set(tilePath + ".isCity", tile.isCity());

            if (tile.isCity() && tile.getCityData() != null) {
                CityData cd = tile.getCityData();
                config.set(tilePath + ".cityData.factories", cd.getFactories());
                config.set(tilePath + ".cityData.population", cd.getPopulation());
                config.set(tilePath + ".cityData.defense", cd.getDefense());
                config.set(tilePath + ".cityData.isCapital", cd.isCapital());
            }
        }

        try {
            config.save(mapFile);
        } catch (IOException e) {
            Bugger.bug("Не удалось сохранить карту '" + mapName + "': " + e.getMessage());
        }
    }
}