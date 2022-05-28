package com.mygdx.game.Domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Starter;
import com.mygdx.game.Structure.MainMenu;
import com.mygdx.game.Util;
import lombok.Data;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
public class TrafficLight {

    Starter starter;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(30);
    int Bandwidth = 300;
    boolean paused = true;
    ImageButton pauseButton;

    public TrafficLight(Starter starter) {
        this.starter = starter;
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageChecked = new TextureRegionDrawable(new Texture("pause_started.png"));
        style.imageUp = new TextureRegionDrawable(new Texture("pause.png"));
        pauseButton = new ImageButton(style);
        pauseButton.setPosition(Gdx.graphics.getWidth() - 84, 20);
        pauseButton.setSize(64, 64);
        pauseButton.setTouchable(Touchable.enabled);
        starter.getMainMenu().getStage().addActor(pauseButton);
    }

    public void register(Intersection intersection) {
        schedule(intersection);
        System.out.println("Registered intersection " + intersection.getName());
    }

    public void run() {
        //scheduler.
        for (Intersection i : MainMenu.intersections) {
            schedule(i);
            System.out.println("Registered intersection " + i.getName());
        }
    }

    private void schedule(Intersection i) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!paused) {
                    boolean longer = false;
                    List<Road> roads = starter.getMainMenu().getRoads();
                    List<Road> connected = roads.stream()
                            .filter(road -> road.getFirst().equals(i) || road.getSecond().equals(i))
                            .sorted((x, y) -> x.getWorkLoad().get() > y.getWorkLoad().get() ? 1 : 0)
                            .collect(Collectors.toList());
                    if (connected.size() >= 2 && Util.getToIntersectionWorkload(connected.get(0), i).get() > Util.getToIntersectionWorkload(connected.get(1), i).get() * 1.5)
                        longer = true;
                    int bandwidthPerRoad = longer ? Bandwidth / (connected.size()) : Bandwidth / (connected.size() - 1);

                    for (Road r : connected) {
                        AtomicInteger input = Util.getToIntersectionWorkload(r, i);
                        if (input.get() == 0)
                            continue;
                        int portion = input.get() / (longer ? connected.size() : connected.size() - 1);
                        int disposable = Math.min(portion, bandwidthPerRoad);
                        if (disposable == 0)
                            return;
                        boolean first = false;
                        for (Road r1 :
                                connected) {


                            if (r != r1) {
                                AtomicInteger output = Util.getFromIntersectionWorkload(r1, i);
                                if (!first) {
                                    output.addAndGet(input.get() % disposable);
                                    if (input.get() - input.get() % disposable < 0)
                                        System.out.println("Error 0001");
                                    input.set(input.get() - input.get() % disposable);

                                    first = true;
                                }
                                for (int temp = 0; temp < (longer && r1 == connected.get(0) ? 2 : 1); temp++) {
                                    output.addAndGet(disposable);
                                    if (input.get() - disposable < 0) {
                                        System.out.println("Error 0002");
                                    }
                                    input.set(input.get() - disposable);

                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}
