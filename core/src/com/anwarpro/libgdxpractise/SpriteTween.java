package com.anwarpro.libgdxpractise;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteTween implements TweenAccessor<Sprite> { //** Tweening a Sprite **//
    public static final int POSITION_X = 1; //** there will one int declaration per object **//
    public static final int POSITION_Y = 2; //** there will one int declaration per object **//
    public static final int SCALE = 3; //** there will one int declaration per object **//
    public static final int SCALE_Y = 4;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.getX();
                return 1; // ** one case for each object - returned one as only 1 value is being changed **//
            case POSITION_Y:
                returnValues[0] = target.getY();
                return 1; // ** one case for each object - returned one as only 1 value is being changed **//

            case SCALE:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 1;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X:
                target.setX(newValues[0]);
                break;
            case POSITION_Y:
                target.setY(newValues[0]);
                break;
            case SCALE:
                target.setScale(newValues[0], newValues[1]);
                break;
            default:
                assert false;
                break;
        }
    }
}
