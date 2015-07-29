package com.jamesrskemp.connerbox2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jamesrskemp.connerbox2d.ConnerBox2d;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 720;
		config.height = 480;
		config.title = "Conner Box2D Tutorial";

		// TODO remove or change later
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;

		new LwjglApplication(new ConnerBox2d(), config);
	}
}
