package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;

import java.util.ArrayList;
import java.util.List;

public class Starter extends ApplicationAdapter {
	List<Intersection> intersections = new ArrayList<>();
	List<Road> roads = new ArrayList<>();
	Viewport viewport;
	SpriteBatch batch;
	OrthographicCamera camera = new OrthographicCamera();
	Vector2 lastClicked = new Vector2();
	Vector2 click = new Vector2();
	Vector2 release = new Vector2();
	boolean isDrugged = false;
	ShapeRenderer sr;
	Stage stage;


	@Override
	public void create () {
		this.viewport = new FitViewport(0, 0, camera);
		stage = new Stage();
		batch = new SpriteBatch();
		resize(800,800);
		sr = new ShapeRenderer();
		Gdx.input.setInputProcessor(new InputAdapter() {

			@Override
			public boolean touchDown (int x, int y, int pointer, int button) {
				y = (int) (camera.viewportHeight-y);
				click.set(x,y);
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				screenY = (int) (camera.viewportHeight-screenY);
				release.set(screenX,screenY);
				if (click.equals(release))//click Check
				{
					if (release.equals(lastClicked))//doubleClick on one position check
					{
						doubleCkick();
						lastClicked.setZero();
						return true;
					}
					lastClicked.set(release);
					click.setZero();
					release.setZero();
					click();
				}
				else // drag click?
				{
					isDrugged = false;
					Intersection first = getIntersection(click.x,click.y);
					Intersection second = getIntersection(release.x,release.y);
					if (first == null || second == null)
					{
						return true;
					}
					roads.add(new Road(first,second));
					System.out.println("Road created!");
				}
				return true;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				screenY = (int) (camera.viewportHeight-screenY);
				isDrugged = true;
				//System.out.println("touchDragged: x="+screenX+" y="+screenY);
				return true;
			}
		});
	}

	private void click() {
		Intersection intersection = getIntersection(lastClicked.x, lastClicked.y);
		if (intersection != null)
		{
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
			{
				intersections.remove(intersection);
				return;
			}
			System.out.println();
			//select
		}
		else
		{
			Intersection e = new Intersection(lastClicked.x, lastClicked.y);
			intersections.add(e);
			e.addListener(new ClickListener(Input.Buttons.RIGHT)
			{
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Dialog dialog = new Dialog("Warning", (new Skin()).get( "dialog", Window.WindowStyle.class)) {
						public void result(Object obj) {
							System.out.println("result "+obj);
						}
					};
					dialog.text("Are you sure you want to quit?");
					dialog.button("Yes", true); //sends "true" as the result
					dialog.button("No", false);  //sends "false" as the result
					dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
					dialog.show(stage);
					super.clicked(event, x, y);
				}
			});
			stage.addActor(e);
		}
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		intersections.forEach(i -> i.render(batch));
		batch.end();
		// TODO: 21.05.2022 Debug section
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(Color.BLACK);
		intersections.forEach(i -> sr.line(new Vector2(i.getArea().x,i.getArea().y), new Vector2(i.getArea().x+i.getArea().height,i.getArea().y+i.getArea().width)));
		roads.forEach(r -> sr.line(r.getFirst().getCenter(), r.getSecond().getCenter()));
		sr.end();
		// TODO: 21.05.2022 End of the debug section
/*		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(Color.BLACK);
		roads.forEach(r -> sr.line(r.getFirst().getCenterCoordinate(), r.getSecond().getCenterCoordinate()));
		sr.end();*/
		if (isDrugged)
		{
			sr.begin(ShapeRenderer.ShapeType.Line);
			sr.setColor(Color.BLACK);
			sr.line(click,new Vector2(Gdx.input.getX(),camera.viewportHeight-Gdx.input.getY()));
			sr.end();
			Gdx.gl.glLineWidth(5);
		}
		stage.draw();
	}

	@Override
	public void dispose () {
		//save?
		intersections.forEach(Intersection::dispose);
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Resize");
		camera.setToOrtho(false, width, height);
		viewport.setWorldSize(width, height);
	}

	private void doubleCkick()
	{
		System.out.println("lastClicked:"+lastClicked);
		System.out.println();
		System.out.println("DoubleClick");
	}

	@Null
	private Intersection getIntersection(float x,float y)
	{
		return intersections.stream()
				.filter(i -> i.inTexture(x,y))
				.findAny().orElse(null);
	}
}
