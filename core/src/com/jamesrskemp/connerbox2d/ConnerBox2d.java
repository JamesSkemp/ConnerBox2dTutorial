package com.jamesrskemp.connerbox2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		// Wants a zoomed in view.
		camera.setToOrtho(false, w / 2, h / 2);

		world = new World(new Vector2(0, -9.8f), false);
		box2dRenderer = new Box2DDebugRenderer();

		player = createBox(70, 140, 70, 70, false);
		platform = createBox(70, 0, 140, 70, true);

//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		box2dRenderer.render(world, camera.combined.scl(Constants.PPM));
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(false, width / 2, height / 2);
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
		box2dRenderer.dispose();
	}

	public void update(float deltaTime) {
		// 6 and 2 seem to be the standard for these.
		world.step(1 / 60f, 6, 2);

		inputUpdate(deltaTime);
		cameraUpdate(deltaTime);
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
		bodyDef.fixedRotation = false;

		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		// Box2D will double what we pass for the size.
		shape.setAsBox(width / 2 / Constants.PPM, height / 2 / Constants.PPM);

		body.createFixture(shape, 1.0f);

		shape.dispose();

		return body;
	}
}
