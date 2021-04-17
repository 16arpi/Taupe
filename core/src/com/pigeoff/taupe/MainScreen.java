package com.pigeoff.taupe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

public class MainScreen implements Screen {
    final TaupeGame game;
    final Preferences prefs;

    Boolean isUp;

    OrthographicCamera camera;
    Viewport viewport;
    BitmapFont fontScore;
    BitmapFont fontBestScore;

    Texture trouHaut;
    Texture trouBas;
    Texture taupe;
    Texture fond;
    Texture pointerTxt;

    Array<Rectangle> rectHaut;
    Array<Rectangle> rectBas;
    Array<Rectangle> rectTaupe;
    Array<Rectangle> rectFond;
    Array<Rectangle> upTaupe;

    ArrayList<Integer> anchors;

    Rectangle title;
    Texture titleImg;

    Vector3 touchPos;

    int score = 0;
    int bestScore;
    long lastTaupUp = TimeUtils.millis();
    long lastTaupDown = TimeUtils.millis();

    private void moveTaupe() {
        int i = MathUtils.random(0, 8);
        long time = TimeUtils.millis();

        if (time - lastTaupDown > 1000) {
            if (!isUp) {
                if (!upTaupe.contains(rectTaupe.get(i), true)) {
                    isUp = true;
                    Rectangle taupe = rectTaupe.get(i);
                    upTaupe.add(taupe);
                    taupeMoveUI(taupe, true);
                    lastTaupUp = TimeUtils.millis();
                    lastTaupDown = TimeUtils.millis();
                }
            }
        }


        if (time - lastTaupDown > 300) {
            if (upTaupe.contains(rectTaupe.get(i), true)) {
                int ix = upTaupe.indexOf(rectTaupe.get(i), true);
                taupeMoveUI(upTaupe.removeIndex(ix), false);
                isUp = false;
                ///lastTaupDown = TimeUtils.millis();
            }
        }
    }

    private void taupeMoveUI(Rectangle taupe, Boolean up) {
        int l = 50;

        if (up) {
            taupe.y = taupe.y + l;
        }
        else {
            taupe.y = taupe.y - l;
        }
    }

    private boolean checkTaupeClick(Rectangle taupe, Vector3 vector) {
        float x1 = taupe.x - 80;
        float x2 = taupe.x + 220;
        float y1 = taupe.y - 80;
        float y2 = taupe.y + 200;
        if (touchPos.x >= x1 && touchPos.x <= x2) {
            if (touchPos.y >= y1 && touchPos.y <= y2) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public MainScreen(final TaupeGame game, final Preferences prefs) {
        this.game = game;
        this.prefs = prefs;
        this.touchPos = new Vector3();
        this.isUp = false;
        //Score
        bestScore = prefs.getInteger("score");
        score = 0;
        anchors = new ArrayList<Integer>();

        //Font
        fontScore = new BitmapFont(Gdx.files.internal("fonts/comic.fnt"),false);
        fontBestScore = new BitmapFont(Gdx.files.internal("fonts/comic.fnt"),false);

        //Tile
        title = new Rectangle(150, 1050-159, 500, 159);
        titleImg = new Texture(Gdx.files.internal("title.png"));

        //Init textures
        trouHaut = new Texture(Gdx.files.internal("trou_haut.png"));
        trouBas = new Texture(Gdx.files.internal("trou_bas.png"));
        taupe = new Texture(Gdx.files.internal("taupe.png"));
        fond = new Texture(Gdx.files.internal("fond.png"));

        //Init camera
        camera= new OrthographicCamera(800, 1050);
        camera.setToOrtho(false, 800, 1050);
        viewport = new FitViewport(800, 1050, this.camera);


        anchors.add(80);
        anchors.add(320);
        anchors.add(560);


        //Add elmnts to arrays
        rectHaut = new Array<Rectangle>();
        rectBas = new Array<Rectangle>();
        rectTaupe = new Array<Rectangle>();
        rectFond = new Array<Rectangle>();

        upTaupe = new Array<Rectangle>();

        int i = 0;
        while (i <= 2) {
            int j = 0;
            while (j <=2) {
                Rectangle trouHautR = new Rectangle();
                Rectangle trouBasR = new Rectangle();
                Rectangle taupeR = new Rectangle();
                Rectangle fondR = new Rectangle();

                //Pos trou haut
                trouHautR.x = anchors.get(i);
                trouHautR.y = anchors.get(j) + 78 + 80;
                //Pos trou bas
                trouBasR.x = anchors.get(i);
                trouBasR.y = anchors.get(j) + 80;
                //Pos taupe
                taupeR.x = anchors.get(i);
                taupeR.y = anchors.get(j) + 80;
                //Pos fond
                fondR.x = anchors.get(i);
                fondR.y = anchors.get(j) + 80;
                //Size
                trouHautR.width = 160;
                trouHautR.height = 80;
                trouBasR.width = 160;
                trouBasR.height = 80;
                taupeR.width = 160;
                taupeR.height = 124;
                fondR.width = 160;
                fondR.height = 160;

                rectBas.add(trouBasR);
                rectHaut.add(trouHautR);
                rectTaupe.add(taupeR);
                rectFond.add(fondR);
                j++;
            }
            i++;
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(107/255f, 112/255f, 93/255f, 1);
        //Gdx.gl.glClearColor( 183/255f, 183/255f, 164/255f, 1 );


        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        //Score label
        GlyphLayout layout = new GlyphLayout(fontScore, String.valueOf(score));
        GlyphLayout layout2 = new GlyphLayout(fontScore, String.valueOf(score));
        fontScore.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        fontBestScore.setColor(221/255f, 190/255f, 169/255f, 1.0f);
        fontScore.draw(game.batch, String.valueOf(score), 800/3*1 - layout.width/2, 90);
        fontBestScore.draw(game.batch, String.valueOf(bestScore), 800/3*2 - layout.width/2, 90);
        fontBestScore.getData().setScale(1);

        game.batch.draw(titleImg, title.x, title.y, title.width, title.height);

        for (Rectangle rect : rectFond) {
            game.batch.draw(fond, rect.x, rect.y, rect.width, rect.height);
        }

        for (Rectangle rect : rectHaut) {
            game.batch.draw(trouHaut, rect.x, rect.y, rect.width, rect.height);
        }

        for (Rectangle rect : rectTaupe) {
            game.batch.draw(taupe, rect.x, rect.y, rect.width, rect.height);
        }

        for (Rectangle rect : rectBas) {
            game.batch.draw(trouBas, rect.x, rect.y, rect.width, rect.height);
        }

        //End draw elements
        moveTaupe();

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

            int ix = 0;
            for(Rectangle taupe : upTaupe) {

                if (checkTaupeClick(taupe, touchPos)) {
                    taupeMoveUI(upTaupe.removeIndex(ix), false);
                    lastTaupDown = TimeUtils.millis();
                    isUp = false;
                    Gdx.input.vibrate(10);
                    score++;
                    if (score > bestScore) {
                        bestScore = score;
                        prefs.putInteger("score", bestScore);
                        prefs.flush();
                    }
                }
                System.out.println("UP");
                ix++;
            }
        }



        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {
        viewport.update(i, i1, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        trouBas.dispose();
        trouHaut.dispose();
        fond.dispose();
        taupe.dispose();
        pointerTxt.dispose();
    }
}
