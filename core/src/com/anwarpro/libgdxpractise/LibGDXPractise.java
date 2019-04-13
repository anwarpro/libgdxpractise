package com.anwarpro.libgdxpractise;

import com.anwarpro.libgdxpractise.entity.BallData;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class LibGDXPractise extends ApplicationAdapter implements GestureDetector.GestureListener, ContactListener {

    public static final int WORLD_W = 400;
    public static final int WORLD_H = 625;
    public static final float BALL_RADIS = 60;
    public static final float RIM_RADIS = 2.5f;

    private float ration = 400 / 625f;

    private World world;
    private OrthographicCamera camera;

    private Body left_rim;
    private Body right_rim;
    private Body front_rim;

    private Body ball;
    private float xpos = 200;
    private float ypos = 547;

    private Sound spawn;
    private Sound whoosh;
    private Sound backboard;
    private Sound score_sound;
    private Sound fail;

    private BallData ballData;

    private TweenManager manager = new TweenManager();
    private Box2DDebugRenderer debugRender;
    private Vector2 start_location;
    private boolean isDown;
    private Vector2 end_location;

    private Sprite ballSprite;
    private Sprite hoopSprite;
    private Sprite sideRimSprite;
    private Sprite frontRimSprite;

    private Sprite[] basket = new Sprite[4];

    private Sprite win[] = new Sprite[5];
    private Sprite lose[] = new Sprite[5];
    private SpriteBatch batch;
    private Texture hoopTex;
    private boolean draw;
    private Tween moveInTween;
    private Tween fadeInTween;

    private Sprite emoji;
    private Tween ballSizeTween;
    private Tween ballThrowTween;

    private int score = 0;
    private int hit = 0;

    private BitmapFont font;

    private OrthographicCamera cameraUI;

    private boolean success = false;
    private boolean readyForThrow;

    public LibGDXPractise() {
        Tween.registerAccessor(CircleShape.class, new CircleShapeAccessor());
        Tween.registerAccessor(Sprite.class, new SpriteTween());
    }

    private void preload() {
        ballSprite = new Sprite(new Texture(Gdx.files.internal("images/ball.png")), 0, 0, 120, 120);
        hoopTex = new Texture(Gdx.files.internal("images/hoop.png"));
        hoopSprite = new Sprite(hoopTex);
        sideRimSprite = new Sprite(new Texture(Gdx.files.internal("images/side_rim.png")));
        frontRimSprite = new Sprite(new Texture(Gdx.files.internal("images/front_rim.png")));

        for (int i = 0; i < 5; i++) {
            win[i] = new Sprite(new Texture(Gdx.files.internal("images/win" + i + ".png")), 160, 160);
            win[i].flip(false, true);
        }

        for (int i = 0; i < 5; i++) {
            lose[i] = new Sprite(new Texture(Gdx.files.internal("images/lose" + i + ".png")), 160, 160);
            lose[i].flip(false, true);
        }

        for (int i = 0; i < 4; i++) {
            basket[i] = new Sprite(new Texture(Gdx.files.internal("images/basket/" + i + ".png")), 82, 177);
            basket[i].flip(false, true);
        }
    }

    @Override
    public void create() {

        preload();

        world = new World(new Vector2(0, -9.8f), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, WORLD_W / 16f, WORLD_H / 16f);
        cameraUI = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debugRender = new Box2DDebugRenderer();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.color = Color.BLACK;

        font = generator.generateFont(parameter);

        batch = new SpriteBatch();

        //sound
        spawn = Gdx.audio.newSound(Gdx.files.internal("audio/spawn.wav"));
        whoosh = Gdx.audio.newSound(Gdx.files.internal("audio/whoosh.ogg"));
        backboard = Gdx.audio.newSound(Gdx.files.internal("audio/backboard.ogg"));
        score_sound = Gdx.audio.newSound(Gdx.files.internal("audio/score.wav"));
        fail = Gdx.audio.newSound(Gdx.files.internal("audio/fail.wav"));

        //texture

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(150 / 16f, 184 / 16f);
        left_rim = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RIM_RADIS / 16f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.restitution = 0.63f;
        left_rim.createFixture(fixtureDef);

        bodyDef.position.set(249 / 16f, 184 / 16f);
        right_rim = world.createBody(bodyDef);
        right_rim.createFixture(fixtureDef);

        ballData = new BallData();

        world.setContactListener(this);

        //ball
        createBall();

        Gdx.input.setInputProcessor(new GestureDetector(this));

    }

    private void createBall() {

        if (ballSizeTween != null) {
            ballSizeTween.kill();
        }

        if (ballThrowTween != null) {
            ballThrowTween.kill();
        }

        spawn.play();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xpos / 16f, ypos / 16f);
        ball = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(60 / 16f);

        ball.createFixture(circleShape, 0);
        ball.setAwake(false);

        ballData.setLunched(false);
        ballData.setBellowHoop(false);

        ball.setUserData(ballData);

        ballSizeTween = Tween.from(ball.getFixtureList().get(0).getShape(), CircleShapeAccessor.TYPE_RADIAS, 2 / 16f)
                .target(36 / 16f)
                .ease(TweenEquations.easeNone)
                .start(manager);

    }

    private void drawReset() {
        batch.setProjectionMatrix(cameraUI.combined);
        font.draw(batch, "Your Score: " + score, -10, 0);
        font.draw(batch, "Touch anywhere to restart", -(Gdx.graphics.getWidth() / 2f) / 2f, -20);
    }

    private void drawGame() {
        batch.setProjectionMatrix(camera.combined);
        batch.draw(hoopSprite, 88 / 16f, 62 / 16f, 244 / 16f, 147 / 16f);

        if (draw) {
            batch.draw(emoji, emoji.getX(), emoji.getY(), 60 / 16f, 60 / 16f);
            if (moveInTween.isFinished()) {
                draw = false;
            }
        }

        batch.draw(sideRimSprite, left_rim.getPosition().x - (2.5f / 16f) / 2f,
                left_rim.getPosition().y - (2.5f / 16f) / 2f,
                5 / 16f, 5 / 16f);

        batch.draw(sideRimSprite, right_rim.getPosition().x - (2.5f / 16f) / 2f,
                right_rim.getPosition().y - (2.5f / 16f) / 2f,
                5 / 16f, 5 / 16f);


        float ballArea = ball.getFixtureList().get(0).getShape().getRadius() * 2;

        batch.draw(ballSprite, ball.getPosition().x - ballArea / 2f,
                ball.getPosition().y - ballArea / 2f,
                ballArea, ballArea);

        if (ball.getLinearVelocity().y > 0) {
            batch.draw(frontRimSprite, 148 / 16f, 182 / 16f, 104 / 16f, 6 / 16f);
        }

        batch.draw(basket[hit], 0, 300 / 16f, 82 / 16f, 177 / 16f);

        if (draw) {
            batch.setProjectionMatrix(cameraUI.combined);
            if (success) {
                font.draw(batch, "Score: " + score, 0, 0);
            } else {
                font.draw(batch, "Opps!", 0, 0);
            }
            batch.setProjectionMatrix(camera.combined);
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        font.dispose();
        fail.dispose();
        whoosh.dispose();
        backboard.dispose();
        spawn.dispose();
        score_sound.dispose();
    }

    @Override
    public void render() {
        camera.update();
        cameraUI.update();

        manager.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (ball.getLinearVelocity().y > 0 && ball.getPosition().y > 188 / 16f && !ballData.isBellowHoop()) {

            ballData.setBellowHoop(true);
            ball.setAwake(true);

            right_rim.setAwake(true);
            left_rim.setAwake(true);

            if (ball.getPosition().x > 151 / 16f && ball.getPosition().x < 249 / 16f && ball.getPosition().y > left_rim.getPosition().y) {

                emoji = win[(new Random().nextInt((4 - 0) + 1))];

                draw = true;
                emoji.setPosition(180 / 16f, 100 / 16f);
                moveInTween = Tween.from(emoji, SpriteTween.POSITION_Y, 1f)
                        .target(150 / 16f)
                        .ease(TweenEquations.easeInOutElastic)
                        .start(manager);

                score++;
                success = true;

                score_sound.play();
                hit++;
            } else {
                emoji = lose[(new Random().nextInt((4) + 1))];

                draw = true;
                emoji.setPosition(180 / 16f, 100 / 16f);
                moveInTween = Tween.from(emoji, SpriteTween.POSITION_Y, 1f)
                        .target(150 / 16f)
                        .ease(TweenEquations.easeInOutElastic)
                        .start(manager);
                success = false;
                fail.play();
                hit++;
            }

        } else if (!ballData.isBellowHoop()) {
            right_rim.setAwake(false);
            left_rim.setAwake(false);
        }

        if (ball.getPosition().y > 1200 / 16f) {
            world.setGravity(new Vector2(0, 0));
            ball.getWorld().destroyBody(ball);
            createBall();
        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        //draw game

        if (hit == 3) {
            drawReset();
            drawGame();
        } else {
            drawGame();
        }

        batch.end();

        if (hit != 3) {
            debugRender.render(world, camera.combined);
        }

        world.step(1 / 60f, 100, 100);


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

    private void launch(float x_traj) {

        if (!ballData.isLunched()) {
            ball.getFixtureList().get(0).getShape().setRadius(36 / 16f);
//            ball.body.setCollisionGroup(collisionGroup);
            ballData.setLunched(true);
            world.setGravity(new Vector2(0, 3000 / 16f));

            ballThrowTween = Tween.to(ball.getFixtureList().get(0).getShape(), CircleShapeAccessor.TYPE_RADIAS, 100 / 16f)
                    .target(36 / 16f)
                    .ease(TweenEquations.easeNone)
                    .start(manager);

//            game.add.tween(ball.scale).to({x :0.6, y :0.6},
//            500, Phaser.Easing.Linear.None, true, 0, 0, false);

            ball.setLinearVelocity(x_traj, -1750 / 16f);
            whoosh.play();
        }

    }


    private void hitRim() {
        backboard.play();
    }

    @Override
    public void beginContact(Contact contact) {
        hitRim();
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (hit == 3) {
            hit = 0;
            score = 0;
            draw = false;
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        float x_traj = -2300 / 16f * velocityX / velocityY;
        Gdx.app.log("Ready", "" + readyForThrow);
        if (hit != 3) {
            launch(x_traj);
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
