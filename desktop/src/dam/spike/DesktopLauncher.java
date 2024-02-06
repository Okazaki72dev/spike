package dam.spike;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(144);
        config.setTitle("Spike");
        config.setWindowIcon("assets/splashes/spikeSplash2.png");

        new Lwjgl3Application(new Spike(), config);
    }
}
