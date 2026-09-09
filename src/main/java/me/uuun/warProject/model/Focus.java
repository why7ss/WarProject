package me.uuun.warProject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Focus {
    private final String name;
    private int cost;
    private Runnable onComplete;
    private boolean completed;
    private double progress;
}