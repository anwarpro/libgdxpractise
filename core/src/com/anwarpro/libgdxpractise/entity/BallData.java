package com.anwarpro.libgdxpractise.entity;

public class BallData {

    private boolean lunched;
    private boolean isBellowHoop;

    public BallData() {
    }

    public boolean isLunched() {
        return lunched;
    }

    public void setLunched(boolean lunched) {
        this.lunched = lunched;
    }

    public boolean isBellowHoop() {
        return isBellowHoop;
    }

    public void setBellowHoop(boolean bellowHoop) {
        isBellowHoop = bellowHoop;
    }
}
