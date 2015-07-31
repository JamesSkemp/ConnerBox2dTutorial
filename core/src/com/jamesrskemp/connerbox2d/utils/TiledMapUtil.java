package com.jamesrskemp.connerbox2d.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by James on 7/30/2015.
 */
public class TiledMapUtil {
	public static void parseTiledObjectLayer(World world, MapObjects mapObjects) {
		for (MapObject mapObject : mapObjects) {
			Shape shape;
			if (mapObject instanceof PolylineMapObject) {
				shape = createPolyline((PolylineMapObject)mapObject);
			} else {
				continue;
			}

			Body body;
			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.StaticBody;
			body = world.createBody(def);
			body.createFixture(shape, 1.0f);

			shape.dispose();
		}
	}

	private static ChainShape createPolyline(PolylineMapObject mapObject) {
		// Transformed vertices gives us better vertices for Box2d.
		float[] vertices = mapObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < worldVertices.length; i++) {
			worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
		}

		// Instead of returning each line segment, this will return a continuous polyline.
		ChainShape chainShape = new ChainShape();
		chainShape.createChain(worldVertices);

		return chainShape;
	}
}
