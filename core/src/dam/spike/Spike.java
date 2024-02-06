package dam.spike;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;


public class Spike extends ApplicationAdapter {
    private SpriteBatch batch;
    private Sprite background;
    private Sprite cannon;
    private Sprite spike;
    private Vector2 spikePosition;
    private Vector2 spikeVelocity;
    private boolean isLaunching = false;
    private boolean spikedLaunched = false;
    private OrthographicCamera camera;

    private float totalDistance = 0;
    private float maxDistance = 0;
    private float originalSpikeWidth;
    private float originalSpikeHeight;
    private Sound sound;
    private Music music;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        loadAssets();
        initializeSprites();
        initializeSpikeParameters();
    }

    private void loadAssets() {
        loadTextures();
        loadSounds();
        loadMusic();
        loadFonts();
    }


    private void loadTextures() {
        background = new Sprite(new Texture("assets/textures/background.png"));
        background.setSize(Gdx.graphics.getWidth() * 4, Gdx.graphics.getHeight() * 4);

        cannon = new Sprite(new Texture("assets/sprites/canon.png"));
        cannon.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        cannon.setPosition(0, 0);

        spike = new Sprite(new Texture("assets/sprites/spike.png"));
        spike.setPosition(cannon.getX() + 50, cannon.getY() + 50);
    }

    private void loadSounds() {
        sound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/cannon.mp3"));
    }

    private void loadMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background.mp3"));
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
    }

    private void loadFonts() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Chunq.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 12;
        fontParameter.borderWidth = 1;
        fontParameter.borderColor= Color.BLACK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

    }

    private void initializeSprites() {
        originalSpikeWidth = spike.getWidth();
        originalSpikeHeight = spike.getHeight();
    }

    private void initializeSpikeParameters() {
        spikePosition = new Vector2(spike.getX(), spike.getY());
        spikeVelocity = new Vector2(0, 0);
    }

    @Override
    public void render() {
        clearScreen();
        handleInput();
        updateSpikePosition();
        updateCamera();
        drawSprites();
        drawHUD();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isLaunching = true;
            playSound(sound);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLaunch();
        }
    }

    private void updateSpikePosition() {
        if (isLaunching && !spikedLaunched) {
            updateSpikeVelocity();
            updateSpikeSize();
            updateTotalDistance();

            if (spikePosition.y <= 0 && !spikedLaunched) {
                spikedLaunched = true;
                updateMaxDistance();
            }
        }
    }

    private void updateSpikeVelocity() {
        if (spikeVelocity.y == 0) {
            spikeVelocity.set(15, 3);
        }

        spikePosition.add(spikeVelocity);
        spikeVelocity.scl(0.9999f);

        if (spikePosition.y > 0) {
            spikeVelocity.y += -0.05f;
        }
    }

    private void updateSpikeSize() {
        spike.setSize(spike.getWidth(), spike.getHeight() * 0.995f);
    }

    private void updateTotalDistance() {
        totalDistance += spikePosition.x / 1000;
        totalDistance = (float) (Math.round(totalDistance * 100.0) / 100.0);
    }

    private void updateMaxDistance() {
        if (totalDistance > maxDistance) {
            maxDistance = totalDistance;
        }
    }

    private void updateCamera() {
        camera.position.set(spikePosition.x + spike.getWidth() + 50, spikePosition.y + spike.getHeight() + 50, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void drawSprites() {
        batch.begin();
        background.draw(batch);
        spike.setPosition(spikePosition.x, spikePosition.y);
        spike.draw(batch);
        cannon.draw(batch);
        batch.end();
    }

    private void drawHUD() {
        float hudX = camera.position.x - camera.viewportWidth / 2 + 10;
        float hudY = camera.position.y + camera.viewportHeight / 2 - 10;

        batch.begin();
        font.draw(batch, "Distancia= " + totalDistance, hudX, hudY);
        font.draw(batch, "Distancia Maxima= " + maxDistance, hudX + 400, hudY);
        batch.end();
    }

    private void playSound(Sound sound) {
        long id = sound.play(0.15f);
    }

    private void resetLaunch() {
        isLaunching = false;
        spikedLaunched = false;
        spikeVelocity.set(0, 0);
        spikePosition.set(cannon.getX() + 50, cannon.getY() + 50);
        totalDistance = 0;
        spike.setSize(originalSpikeWidth, originalSpikeHeight);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        background.getTexture().dispose();
        cannon.getTexture().dispose();
        spike.getTexture().dispose();
        music.dispose();
        sound.dispose();
    }
}
