package me.uuun.warProject.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder(builderMethodName = "build")
public class Troop {
    @NotNull private Tile tile;
    @Builder.Default private final TroopType troopType = TroopType.INFANTRY;
    @Builder.Default private double experience = 0;
    @Builder.Default private Set<Tile> movementPath = new LinkedHashSet<>();
    @Builder.Default private int health = 100;
    @Builder.Default @Nullable private TroopAttack troopAttack = null;

    public static TroopBuilder builder(@NotNull Tile tile) {
        Objects.requireNonNull(tile, "Tile is required in troop : " + tile);
        return build().tile(tile);
    }
}