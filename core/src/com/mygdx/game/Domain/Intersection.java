package com.mygdx.game.Domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class Intersection extends Button {
    int halfSize = 32;
    Vector2 center = new Vector2();
    Texture texture = new Texture("intersection.png");
    Rectangle area = new Rectangle();

    public Intersection(float x, float y) {
        this.setDebug(true);
        this.setPosition(x-halfSize,y-halfSize);
        this.setSize(64,64);
        center.set(x,y);
        area.set(x-halfSize,y-halfSize,64,64);
    }

    public void render (Batch batch)
    {
        batch.draw(texture, center.x-halfSize, center.y-halfSize);
    }

    public void dispose()
    {
        texture.dispose();
    }

    public boolean inTexture(float x,float y)
    {
        return area.contains(x,y);
    }

    public Rectangle getArea() {
        return area;
    }

    public Vector2 getCenter() {
        return center;
    }
}
