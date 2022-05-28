package com.mygdx.game.Structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Starter;
import com.mygdx.game.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class MainMenu extends Starter {
    public MainMenu(Stats stats) {
        this.stats = stats;
    }

    Window info;
    boolean hovering = false;

    Vector2 lastClicked = new Vector2();
    Vector2 click = new Vector2();
    Vector2 release = new Vector2();

    @Override
    public void create() {
    }

    Stage stage;
    boolean isDrugged = false;
    public static List<Intersection> intersections = new ArrayList<>();
    List<Road> roads = new ArrayList<>();

    {
        stage = new Stage();
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getClick().set(x, y);

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (stats.getScene() == Scene.MAIN) {
                    getRelease().set(x, y);

                    if (getRelease().equals(getClick())) {
                        getLastClicked().set(getRelease());
                        getClick().setZero();
                        getRelease().setZero();
                        click();
                    } else {
                        isDrugged = false;
                        Intersection first = getIntersection(getClick().x, getClick().y);
                        Intersection second = getIntersection(getRelease().x, getRelease().y);
                        if (first == null || second == null || first.equals(second)) {
                            return;
                        }
                        roads.add(new Road(first, second));
                    }
                }


            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                isDrugged = true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.ESCAPE) {
                    stats.getSelected().deselect();
                    stats.setSelected(null);
                }
                if (keyCode == Input.Keys.SPACE) {
                    starter.getTrafficLight().setPaused(!starter.getTrafficLight().isPaused());
                    starter.getTrafficLight().getPauseButton().setChecked(!starter.getTrafficLight().isPaused());
                }
                return true;
            }


        });
    }

    private void click() {
        Intersection intersection = getIntersection(getLastClicked().x, getLastClicked().y);
        if (intersection == null && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            return;
        if (intersection != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                intersections.remove(intersection);
                stage.getActors().removeValue(intersection, false);
                roads.removeIf(r -> (r.getFirst().equals(intersection) || r.getSecond().equals(intersection)));
                return;
            } else {
                if (stats.getSelected() != null) {
                    {
                        if (stats.getSelected() != null && intersection.equals(stats.getSelected())) {
                            stats.setScene(Scene.INTERSECTION_INFO);
                            return;
                        }
                    }
                    stats.getSelected().deselect();
                }
                System.out.println("Selected!");
                stats.setSelected(intersection);
                stats.getSelected().select();
            }
        } else {
            System.out.println("Point g");
            Intersection e = new Intersection(getLastClicked().x, getLastClicked().y);
            intersections.add(e);
            System.out.println("Intersection created");
            System.out.println(e);
            stage.addActor(e);
            starter.getTrafficLight().register(e);
        }
    }

    public void redraw() {
        for (Actor actor : stage.getActors())
            if (actor instanceof Label)
                stage.getActors().removeValue(actor, false);
        for (Intersection i : intersections) {
            Label name = new Label(i.getName(), Stats.skin);
            name.setAlignment(Align.center);
            name.setPosition(i.getCenter().x - name.getWidth() / 2, i.getCenter().y - i.getHalfSize());

            stage.addActor(name);
        }
        for (Road road : roads) {
            Label name = new Label(road.getName(), Stats.skin);
            name.setAlignment(Align.center);
            name.setTouchable(Touchable.enabled);
            name.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("x: " + x + " y: " + y);
                    boolean b = super.touchDown(event, x, y, pointer, button);
                    System.out.println("Boolean: " + b);
                    return b;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println("Clicked");
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        roads.remove(road);
                        stage.getActors().removeValue(name, false);
                    }

                }
            });
            Vector2 center = new Vector2((road.getFirst().getX() + road.getSecond().getX()) / 2, (road.getFirst().getY() + road.getSecond().getY()) / 2);
            name.setPosition((center.x - name.getWidth() / 2), (center.y));
            name.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    hovering = true;
                    if (info == null) {
                        info = new Window("info", Stats.skin);
                        info.setSkin(Stats.skin);
                        Label name = new Label("Name: " + road.getName(), Stats.skin);
                        info.add(name);
                        info.row();
                        Label workload = new Label("Workload: " + road.getWorkLoad(), Stats.skin);
                        info.add(workload);
                        info.row();
                        Label reverseWorkload = new Label("Reverse workload: " + road.getReverseWorkload(), Stats.skin);
                        info.add(reverseWorkload);
                        info.row();
                        Label length = new Label("Length: " + road.getSizePoints(), Stats.skin);
                        info.add(length);
                        info.row();
                        info.setWidth(Stream.of(name.getWidth(), workload.getWidth(), reverseWorkload.getWidth(), length.getWidth()).max(Float::compareTo).get() + 25);
                    }
                    stage.addActor(info);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    hovering = false;
                    stage.getActors().removeValue(info, false);
                    info = null;

                }
            });
            stage.addActor(name);
            Label workload = new Label("w: " + road.getWorkLoad(), Stats.skin);
            workload.setAlignment(Align.center);
            workload.setPosition(center.x - workload.getWidth() / 2, center.y - 15);
            stage.addActor(workload);
            Label reverseWorkload = new Label("rw: " + road.getReverseWorkload(), Stats.skin);
            reverseWorkload.setAlignment(Align.center);
            reverseWorkload.setPosition(center.x - reverseWorkload.getWidth() / 2, center.y - 30);
            stage.addActor(reverseWorkload);
        }
    }

    @Override
    public void render() {


        if (info != null) {
            Vector2 vector2 = Util.screenCoord(new Vector2(Gdx.input.getX() + 16, Gdx.input.getY() - 16));
            info.setPosition(vector2.x, vector2.y);
        }

        stats.getSr().begin(ShapeRenderer.ShapeType.Line);
        stats.getSr().setColor(Color.BLACK);
        roads.forEach(r -> stats.getSr().line(r.getFirst().getCenter(), r.getSecond().getCenter()));
        stats.getSr().end();
        if (info != null)
            stage.getActors().swap(stage.getActors().indexOf(info, false), stage.getActors().size - 1);
        stage.draw();
        stage.act();

        // Debug section
        if (Starter.debug) {
            stats.getSr().begin(ShapeRenderer.ShapeType.Line);
            stats.getSr().setColor(Color.BLACK);
            intersections.forEach(i -> stats.getSr().line(new Vector2(i.getArea().x, i.getArea().y), new Vector2(i.getArea().x + i.getArea().height, i.getArea().y + i.getArea().width)));
            stats.getSr().end();
        }


        if (isDrugged) {
            stats.getSr().begin(ShapeRenderer.ShapeType.Line);
            stats.getSr().setColor(Color.BLACK);
            stats.getSr().line(getClick(), new Vector2(Gdx.input.getX(), stats.getCamera().viewportHeight - Gdx.input.getY()));
            stats.getSr().end();
        }
    }

    @Null
    public Intersection getIntersection(float x, float y) {
        return intersections.stream()
                .filter(i -> i.inTexture(x, y))
                .findAny().orElse(null);
    }
}
