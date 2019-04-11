package com.anwarpro.libgdxpractise;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class LibGDXPractise extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture texture;
    private Texture CircTxt, ElasticTxt, QuadTxt; //** text **//
    private Sprite sprite1, sprite2, sprite3;
    private TweenManager manager1, manager2, manager3;
    private long startTime;
    private long delta;
    private float w, h;

    @Override
    public void create() {
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 625);
        batch = new SpriteBatch();

        texture = new Texture("circle.png");
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        CircTxt = new Texture("circleTxt.png");
        CircTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        ElasticTxt = new Texture("circleTxt.png");
        ElasticTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        QuadTxt = new Texture("circleTxt.png");
        QuadTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        sprite1 = new Sprite(texture);
        sprite1.setPosition(50, 0.25f * h);

        sprite2 = new Sprite(sprite1); //** sprite2 identical to sprite1 **//
        sprite2.setY(0.5f * h);   //** except for the y position **//

        sprite3 = new Sprite(sprite1); //** sprite3 identical to sprite1 **//
        sprite3.setY(0.75f * h);  //** except for the y position **//

        Tween.registerAccessor(Sprite.class, new SpriteTween());
        manager1 = new TweenManager();
        Tween.to(sprite1, SpriteTween.POSITION_X, 1000f) //** tween POSITION_X for a duration **//
                .target(w - 100) // ** final POSITION_X **//
                .ease(TweenEquations.easeInOutQuad) //** easing equation **//
                .repeat(10, 1000f) //** ten more times **//
                .start(manager1); //** start it
        manager2 = new TweenManager();
        Tween.to(sprite2, SpriteTween.POSITION_X, 1000f) //** tween POSITION_X for a duration **//
                .target(w - 100) // ** final POSITION_X **//
                .ease(TweenEquations.easeInOutCirc) //** easing equation **//
                .repeat(10, 1000f) //** ten more times **//
                .start(manager2);
        manager3 = new TweenManager();
        Tween.to(sprite3, SpriteTween.POSITION_X, 1000f) // ** tween POSITION_X for a duration **/
                .target(w - 100) // ** final POSITION_X **//
                .ease(TweenEquations.easeInOutElastic) //** easing equation **//
                .repeat(10, 1000f) //** ten more times **//
                .start(manager3);
        startTime = TimeUtils.millis();
    }


    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        //font.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        delta = (TimeUtils.millis() - startTime) / 1000; // **get time delta **//
        manager1.update(delta); //** update sprite1 **//
        manager2.update(delta); //** update sprite2 **//
        manager3.update(delta); //** update sprite3 **//
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(ElasticTxt, 150, 0.75f * h + 50);
        batch.draw(CircTxt, 150, 0.5f * h + 50);
        batch.draw(QuadTxt, 150, 0.25f * h + 50);
        sprite1.draw(batch);
        sprite2.draw(batch);
        sprite3.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
