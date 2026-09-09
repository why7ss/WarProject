package me.uuun.warProject.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Getter
public class Game {
    @Builder.Default private final Long timestampStarted = System.currentTimeMillis();
    @Builder.Default private Set<Country> countries = new LinkedHashSet<>();
    @Builder.Default private final String mapName = "ww2";

    @Nullable @Setter private BukkitRunnable tickRunnable;
}