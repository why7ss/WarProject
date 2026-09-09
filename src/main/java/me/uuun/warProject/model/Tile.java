package me.uuun.warProject.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = {"x", "z"})
public class Tile {
    private final int x;
    private final int z;

    private final boolean isCity;
    @Nullable private final CityData cityData;
}