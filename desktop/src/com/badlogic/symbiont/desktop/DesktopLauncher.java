package com.badlogic.symbiont.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.symbiont.SymbiontMain;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Symbiont";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new SymbiontMain(), config);
	}
}
