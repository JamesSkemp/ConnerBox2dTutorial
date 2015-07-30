package com.jamesrskemp.connerbox2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jamesrskemp.connerbox2d.utils.Constants;

public class ConnerBox2d extends ApplicationAdapter {
	private static final String TAG = ConnerBox2d.class.toString();

	OrthographicCamera camera;

	World world;
	Box2DDebugRenderer box2dRenderer;

	Body player, platform;

	final float SCALE = 1.5f;

	SpriteBatch batch;
	Texture img;

	OrthogonalTiledMapRenderer tiledMapRenderer;
	TiledMap map;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		// Wants a zoomed in view.
		camera.setToOrtho(false, w / SCALE, h / SCALE);

		world = new World(new Vector2(0, -9.8f), false);
		box2dRenderer = new Box2DDebugRenderer();

		player = createBox(175, 210, 70, 70, false);
		platform = createBox(70, 35, 280, 70, true);

		batch = new SpriteBatch();
		img = new Texture("images/alienGreen_square.png");

		map = new TmxMapLoader().load("images/tilemap/wood.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		box2dRenderer.render(world, camera.combined.scl(Constants.PPM));

		tiledMapRenderer.render();

		batch.begin();
		batch.draw(img, player.getPosition().x * Constants.PPM - img.getWidth() / 2, player.getPosition().y * Constants.PPM - img.getHeight() / 2);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(false, width / SCALE, height / SCALE);
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		box2dRenderer.dispose();
		batch.dispose();
		map.dispose();
		tiledMapRenderer.dispose();
	}

	public void update(float deltaTime) {
		// 6 and 2 seem to be the standard for these.
		world.step(1 / 60f, 6, 2);

		inputUpdate(deltaTime);
		cameraUpdate(deltaTime);
		tiledMapRenderer.setView(camera);
		batch.setProjectionMatrix(camera.combined);
	}

	public void inputUpdate(float deltaTime) {
		int horizontalForce = 0;

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			horizontalForce -= 1;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			horizontalForce += 1;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			player.applyForceToCenter(0, 300, false);
		}

		player.setLinearVelocity(horizontalForce * 5, player.getLinearVelocity().y);
	}

	public void cameraUpdate(float deltaTime) {
		Vector3 position = camera.position;
		position.x = player.getPosition().x * Constants.PPM;
		position.y = player.getPosition().y * Constants.PPM;
		camera.position.set(position);

		camera.update();

//		Gdx.app.log(TAG, "Camera position " + camera.position);
	}

	public Body createBox(float x, float y, int width, int height, boolean isStatic) {
		Body body;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
		bodyDef.fixedRotation = true;

		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		// Box2D will double what we pass for the size.
		shape.setAsBox(width / 2 / Constants.PPM, height / 2 / Constants.PPM);

		body.createFixture(shape, 1.0f);

		shape.dispose();

		return body;
	}
}
