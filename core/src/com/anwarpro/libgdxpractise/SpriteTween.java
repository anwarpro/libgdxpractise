package com.anwarpro.libgdxpractise;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteTween implements TweenAccessor<Sprite> { //** Tweening a Sprite **//
    public static final int POSITION_X = 1; //** there will one int declaration per object **//

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X:
                returnValues[0] = target.getX();
                return 1; // ** one case for each object - returned one as only 1 value is being changed **//
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
            default:
                assert false;
                break;
        }
    }
}
