package me.uuun.warProject.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.uuun.warProject.bug.Bugger;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class Country {
    @Builder.Default private final Set<Tile> tiles = new HashSet<>();
    @Builder.Default private final Set<Country> alliances = new HashSet<>();
    @Builder.Default private final Set<Justification> justifications = new HashSet<>();
    @Builder.Default private final Set<Country> wars = new HashSet<>();
    @Builder.Default private final Set<Research> researches = new HashSet<>();
    @Builder.Default private final Set<Focus> focuses = new HashSet<>();
    private final String name;
    private final String displayName;
    @Builder.Default private final NamedTextColor color = NamedTextColor.WHITE;
    @Builder.Default private volatile long manPower = 20000;
    @Builder.Default private volatile long money = 35000;
    @Builder.Default private volatile long politicalPower = 45;
    @Builder.Default private volatile double attackBuff = 0;
    @Builder.Default private volatile double defenseBuff = 0;
    @Builder.Default private volatile double fabricProductionBuff = 0;
    @Builder.Default private volatile double fabricPriceBuff = 0;
    private Ideology ideology;
    @Nullable private volatile Player player;

    public void addAlliance(Country country) {
        if (country == null) {
            Bugger.bug("Tried add alliance when alliance country is null, for country: " + this.name);
            return;
        }
        this.alliances.add(country);
    }

    public void removeAlliance(Country country) {
        if (country == null) {
            Bugger.bug("Tried remove alliance when alliance country is null, for country: " + this.name);
            return;
        }
        this.alliances.remove(country);
    }

    @Nullable
    public Tile getCapital(){
        for (Tile tile : this.tiles) {
            if(tile == null) {
                Bugger.warn("Tile in GetCapital for is null, for country: " + this.name);
                continue;
            }
            CityData cityData = tile.getCityData();
            if(!tile.isCity() || cityData == null) continue;
            if(cityData.isCapital()){
                return tile;
            }
        }

        Bugger.bug("GetCapital got null for country: " + this.name);
        return null;
    }
}