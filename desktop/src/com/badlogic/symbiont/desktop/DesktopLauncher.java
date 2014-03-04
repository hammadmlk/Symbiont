package com.badlogic.symbiont.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.symbiont.SymbiontMain;

public class DesktopLauncher {
	public static void main (String[] args) {
        if (args.length > 0 && args[0].equals("--emit-points")) {
            SymbiontMain.EMIT_POINTS = true;
        }
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Symbiont";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new SymbiontMain(), config);
	}
}
