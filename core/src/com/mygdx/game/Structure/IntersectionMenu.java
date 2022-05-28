package com.mygdx.game.Structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Starter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mygdx.game.Util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntersectionMenu extends Starter {

    TextField nameField;
    Stage stage = new Stage();

    public IntersectionMenu(Stats stats) {
        this.stats = stats;
    }

    @Override
    public void create() {
        Vector2 vector = new Vector2();
        nameField = new TextField("Placeholder", Stats.skin);
        nameField.setWidth(200);
        screenCoord(vector.set(80, 40));
        nameField.setPosition(vector.x, vector.y);
        stage.addActor(nameField);
        Label nameLabel = new Label("Name:", Stats.skin);
        screenCoord(vector.set(10, 40));
        nameLabel.setPosition(vector.x, vector.y);
        stage.addActor(nameLabel);
        AtomicInteger counter = new AtomicInteger(0);
        getRoads().stream()
                .filter(r -> r.hasIntersection(stats.getSelected()))
                .forEachOrdered(r ->
                {
                    TextButton button = new TextButton(r.getName(), Stats.skin);
                    screenCoord(vector.set(300, 40 + 60 * counter.getAndIncrement()));
                    button.addListener(new ClickListener() {
                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            stats.setSelectedRoad(r);
                            stats.scene = Scene.ROAD_MENU;
                        }
                    });
                    button.setPosition(vector.x, vector.y);
                    stage.addActor(button);
                });
        ImageButton button = exitButton();
        button.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    stats.setScene(Scene.MAIN);
                    stats.getSelected().deselect();
                    stats.setSelected(null);
                    return;
                }
                if (MainMenu.intersections.stream().filter(i -> i != stats.selected).anyMatch(i -> i.getName().equals(nameField.getText()))) {
                    confirmWindow("Error", "It is not possible to have 2 intersections with the same name!", stage);
                    cancel();
                } else {
                    stats.getSelected().setName(nameField.getText());
                    stats.setScene(Scene.MAIN);
                    stats.getSelected().deselect();
                    stats.setSelected(null);
                }
            }
        });
        stage.addActor(button);

    }

    public void reset() {
        stage.getActors().clear();
        create();
    }


    @Override
    public void render() {
        stage.draw();
    }


    List<Road> getRoads() {
        return stats.getStarter().getMainMenu().getRoads();
    }
}
