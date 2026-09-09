package me.uuun.warProject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Justification {
    private final Country initiator;
    private final Country target;
    @Setter private int progress = 0;
}