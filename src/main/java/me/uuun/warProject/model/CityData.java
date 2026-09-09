package me.uuun.warProject.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CityData {
    @Builder.Default private int factories = 0;
    @Builder.Default private long population = 0;
    @Builder.Default private int defense = 0;
    private final String displayName;
    @Builder.Default private final boolean isCapital = false;
}