package com.badlogic.symbiont.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.symbiont.SymbiontMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Symbion";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new SymbiontMain(), config);
	}
}
