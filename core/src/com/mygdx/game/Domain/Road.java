package com.mygdx.game.Domain;

public class Road {
    Intersection first;
    Intersection second;

    public Road(Intersection first, Intersection second) {
        this.first = first;
        this.second = second;
    }

    public Intersection getFirst() {
        return first;
    }

    public Intersection getSecond() {
        return second;
    }
}
