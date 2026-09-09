package me.uuun.warProject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Research {
    private final String name;
    private final int cost;
    private boolean completed;
    private double progress;
}