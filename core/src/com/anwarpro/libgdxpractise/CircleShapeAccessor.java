package com.anwarpro.libgdxpractise;

import com.badlogic.gdx.physics.box2d.CircleShape;

import aurelienribon.tweenengine.TweenAccessor;

public class CircleShapeAccessor implements TweenAccessor<CircleShape> {
    public static final int TYPE_RADIAS = 1;

    @Override
    public int getValues(CircleShape target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case TYPE_RADIAS:
                returnValues[0] = target.getRadius();
                return 2;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(CircleShape target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case TYPE_RADIAS:
                target.setRadius(newValues[0]);
                break;
            default:
                assert false;
                break;
        }
    }
}
