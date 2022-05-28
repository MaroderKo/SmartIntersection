package com.mygdx.game.Structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Starter;
import com.mygdx.game.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

import static com.mygdx.game.Util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoadMenu extends Starter {
    Stage stage = new Stage();

    TextField nameField;
    TextField sizeField;
    TextField workloadField;
    TextField reverseWorkloadField;

    public RoadMenu(Stats stats) {
        this.stats = stats;
    }
    @Override
    public void create() {
        Vector2 vector = new Vector2();
        nameField = new TextField("Placeholder", Stats.skin);
        sizeField = new TextField("Placeholder", Stats.skin);
        workloadField = new TextField("Placeholder", Stats.skin);
        reverseWorkloadField = new TextField("Placeholder", Stats.skin);
        List<TextField> fields = Arrays.asList(nameField,sizeField,workloadField,reverseWorkloadField);
        List<String> names = Arrays.asList("Name:","Size:","Workload:","Reverse\nworkload: ");
        for (int i = 0; i < fields.size(); i++)
        {
            fields.get(i).setWidth(200);
            screenCoord(vector.set(100,40+(60*i)));
            fields.get(i).setPosition(vector.x, vector.y);
            stage.addActor(fields.get(i));
            Label textLabel = new Label(names.get(i), Stats.skin);
            screenCoord(vector.set(10,40+(60*i)));
            textLabel.setPosition(vector.x, vector.y);
            stage.addActor(textLabel);
        }
        nameField.setText(stats.getSelectedRoad().getName());
        sizeField.setText(String.valueOf(stats.getSelectedRoad().getSizePoints()));
        workloadField.setText(String.valueOf(stats.getSelectedRoad().getWorkLoad()));
        reverseWorkloadField.setText(String.valueOf(stats.getSelectedRoad().getReverseWorkload()));
        ImageButton button = exitButton();
        button.addListener(new ClickListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("TouchDown");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    stats.setScene(Scene.INTERSECTION_INFO);
                    return;
                }
                try {
                    int sizePoints = Integer.parseInt(sizeField.getText());
                    int carPoint = Integer.parseInt(workloadField.getText());
                    if (Util.roadNameExist(nameField.getText(),stats.getSelectedRoad()))
                        throw new IllegalStateException();

                    stats.getSelectedRoad().setName(nameField.getText());
                    stats.getSelectedRoad().setSizePoints(sizePoints);
                    stats.getSelectedRoad().getWorkLoad().set(carPoint);
                    stats.setScene(Scene.INTERSECTION_INFO);
                }
                catch (NumberFormatException e)
                {
                    confirmWindow("Error", "Wrong value in Integer fields!",stage);
                    cancel();
                }
                catch (IllegalStateException e)
                {
                    confirmWindow("Error", "Road name must be unique!",stage);
                    cancel();
                }
            }


        });
        stage.addActor(button);
    }

    @Override
    public void render() {
        stage.draw();
    }

    public void reset() {
        stage.getActors().clear();
        create();
    }
}
