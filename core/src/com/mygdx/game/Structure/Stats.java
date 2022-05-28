package com.mygdx.game.Structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Starter;

@lombok.Data
public class Stats {
    private Starter starter;

    public Stats(Starter starter) {
        this.starter = starter;
    }

    Viewport viewport;
    public static Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
    OrthographicCamera camera = new OrthographicCamera();
    Scene scene = Scene.MAIN;
    public static int Counter = 1;
    Road selectedRoad;

    ShapeRenderer sr = new ShapeRenderer();
    Intersection selected;

}
