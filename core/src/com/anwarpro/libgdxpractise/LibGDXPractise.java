package com.anwarpro.libgdxpractise;

import com.anwarpro.libgdxpractise.entity.BallData;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class LibGDXPractise extends ApplicationAdapter implements InputProcessor, ContactListener {

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

    private Sprite win[] = new Sprite[5];
    private Sprite lose[] = new Sprite[5];
    private SpriteBatch batch;
    private Texture hoopTex;

    public LibGDXPractise() {
        Tween.registerAccessor(CircleShape.class, new CircleShapeAccessor());
    }

    private void preload() {
        ballSprite = new Sprite(new Texture(Gdx.files.internal("images/ball.png")));
        hoopTex = new Texture(Gdx.files.internal("images/hoop.png"));
        hoopSprite = new Sprite(hoopTex, 88, 62);
        sideRimSprite = new Sprite(new Texture(Gdx.files.internal("images/side_rim.png")));
        frontRimSprite = new Sprite(new Texture(Gdx.files.internal("images/front_rim.png")));

        for (int i = 0; i < 5; i++) {
            win[i] = new Sprite(new Texture(Gdx.files.internal("images/win" + i + ".png")));
        }

        for (int i = 0; i < 5; i++) {
            lose[i] = new Sprite(new Texture(Gdx.files.internal("images/lose" + i + ".png")));
        }
    }

    @Override
    public void create() {

        preload();

        world = new World(new Vector2(0, -9.8f), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, WORLD_W / 16f, WORLD_H / 16f);
        debugRender = new Box2DDebugRenderer();

        batch = new SpriteBatch();

        //sound
        spawn = Gdx.audio.newSound(Gdx.files.internal("audio/spawn.wav"));
        whoosh = Gdx.audio.newSound(Gdx.files.internal("audio/whoosh.ogg"));
        backboard = Gdx.audio.newSound(Gdx.files.internal("audio/backboard.ogg"));
        score_sound = Gdx.audio.newSound(Gdx.files.internal("audio/score.wav"));
        fail = Gdx.audio.newSound(Gdx.files.internal("audio/fail.wav"));

        //texture

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(150 / 16f, 184 / 16f);
        left_rim = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RIM_RADIS / 16f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        left_rim.createFixture(fixtureDef);

        bodyDef.position.set(249 / 16f, 184 / 16f);
        right_rim = world.createBody(bodyDef);
        right_rim.createFixture(fixtureDef);

        ballData = new BallData();

        world.setContactListener(this);

        //ball
        createBall();

    }

    private void createBall() {
        spawn.play();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xpos / 16f, ypos / 16f);
        ball = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(BALL_RADIS / 16f);

        ball.createFixture(circleShape, 0);
        ball.setAwake(false);

        ballData.setLunched(false);
        ballData.setBellowHoop(false);

        ball.setUserData(ballData);
        Tween.from(ball.getFixtureList().get(0).getShape(), CircleShapeAccessor.TYPE_RADIAS, 100)
                .target(36 / 16f)
                .ease(TweenEquations.easeNone)
                .start(manager);
        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void dispose() {
        world.dispose();
    }

    @Override
    public void render() {

        manager.update(Gdx.graphics.getDeltaTime());
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (ball.getLinearVelocity().y > 0) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(148 / 16f, 182 / 16f);
            front_rim = world.createBody(bodyDef);
        }

        if (ball.getLinearVelocity().y > 0 && ball.getPosition().y > 188 / 16 && !ballData.isBellowHoop()) {
            ballData.setBellowHoop(true);
            ball.setAwake(true);
            float rand = (float) Math.floor(Math.random() * 5);
            if (ball.getPosition().x > 151 / 16f && ball.getPosition().x < 249 / 16f) {
                score_sound.play();
            } else {
                fail.play();
            }
        }

        if (ball.getPosition().y > 1200 / 16f) {
            world.setGravity(new Vector2(0, 0));
            ball.getWorld().destroyBody(ball);
            createBall();
        }

        debugRender.render(world, camera.combined);
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean bodies = ball.getFixtureList().get(0).testPoint(screenX / 16f, screenY / 16f);
        if (bodies) {
            start_location = new Vector2(screenX / 16f, screenY / 16f);
            isDown = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isDown) {
            isDown = false;
            end_location = new Vector2(screenX / 16f, screenY / 16f);

            if (end_location.y < start_location.y) {
                Vector2 slope = new Vector2(end_location.x - start_location.x, end_location.y - start_location.y);

                float x_traj = -2300 * slope.x / slope.y;
                launch(x_traj);
            }
        }
        return false;
    }

    private void launch(float x_traj) {

        if (!ballData.isLunched()) {
            ball.getFixtureList().get(0).getShape().setRadius(36 / 16f);
//            ball.body.setCollisionGroup(collisionGroup);
            ballData.setLunched(true);
            world.setGravity(new Vector2(0, 3000 / 16f));

            Tween.to(ball.getFixtureList().get(0).getShape(), CircleShapeAccessor.TYPE_RADIAS, 500)
                    .target(60 / 16f)
                    .ease(TweenEquations.easeNone)
                    .start(manager);

//            game.add.tween(ball.scale).to({x :0.6, y :0.6},
//            500, Phaser.Easing.Linear.None, true, 0, 0, false);

            ball.setLinearVelocity(x_traj, -1750 / 16f);
            whoosh.play();
        }

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
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
}
