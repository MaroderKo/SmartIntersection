package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Structure.Stats;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {
    static Stage stage = new Stage();

    public static Vector2 screenCoord(Vector2 vector) {
        return stage.screenToStageCoordinates(vector);
    }

    public static ImageButton exitButton() {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("exit.png"))));
        button.setPosition(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight() - 30);
        return button;
    }

    public static String getStringFromFile(String path, int lineNum) {
        LineIterator it = IOUtils.lineIterator(
                new BufferedReader(Gdx.files.internal(path).reader()));
        for (int lineNumber = 0; it.hasNext(); lineNumber++) {
            String line = it.next();
            if (lineNumber == lineNum) {
                return line;
            }
        }
        throw new RuntimeException("Street reader is out of range!");
    }

    public static Window confirmWindow(String title, String message, Stage stage)
    {
        Window pause = new Window(title, Stats.skin);
        pause.setMovable(false);
        Table table = new Table();
        Label errorMessage = new Label(message,Stats.skin);
        table.add(errorMessage);
        table.row();
        TextButton ok = new TextButton("Ok", Stats.skin);
        ok.addListener(new ClickListener()
        {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                stage.getActors().removeValue(pause,false);
                stage.act();
            }
        });
        table.add(ok);
        pause.add(table);
        pause.pack();
        float newWidth = errorMessage.getWidth()+20, newHeight = 200;
        pause.setBounds((Gdx.graphics.getWidth() - newWidth ) / 2,
                (Gdx.graphics.getHeight() - newHeight ) / 2, newWidth , newHeight );
        stage.addActor(pause);
        return pause;
    }

    /**
     *
     * @param road observer road
     * @param intersection searched intersection
     * @return workload from road that directed to intersection
     */

    public static AtomicInteger getToIntersectionWorkload(Road road, Intersection intersection)//Если конечная точка то вернёт ворклоад иначе реверс
    {
        if (road.getSecond().equals(intersection))
            return road.getWorkLoad();
        else
            return road.getReverseWorkload();
    }

    /**
     *
     * @param road observer road
     * @param intersection searched intersection
     * @return workload from road that directed from intersection
     */

    public static AtomicInteger getFromIntersectionWorkload(Road road, Intersection intersection)//Если отправная точка то вернёт ворклоад
    {
        if (road.getFirst().equals(intersection))
            return road.getWorkLoad();
        else
            return road.getReverseWorkload();
    }

    public static boolean roadNameExist(String str, Road road)
    {
        return Starter.starter.getMainMenu().getRoads().stream().filter(r -> r.getName().equals(str)).anyMatch(r -> !r.equals(road));
    }

}
