package com.mygdx.game.Domain;

import com.mygdx.game.Util;
import lombok.Data;
import lombok.NonNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Road {
    Intersection first;
    Intersection second;
    String name;
    int sizePoints = 750;
    AtomicInteger workLoad = new AtomicInteger(new Random().nextInt(500) + 300);
    AtomicInteger reverseWorkload = new AtomicInteger();

    public Road(Intersection first, Intersection second) {
        this.first = first;
        this.second = second;
        do {
            name = Util.getStringFromFile("streets.txt", new Random().nextInt(515));
        } while (Util.roadNameExist(name, this));

    }

    public boolean hasIntersection(@NonNull Intersection intersection) {
        return first.equals(intersection) || second.equals(intersection);
    }

    @Override
    public String toString() {
        return "Name=" + name;
    }


}
