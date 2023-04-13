package com.android.server.lights;
/* loaded from: classes.dex */
public abstract class LogicalLight {
    public abstract void pulse();

    public abstract void setBrightness(float f);

    public abstract void setColor(int i);

    public abstract void setFlashing(int i, int i2, int i3, int i4);

    public abstract void turnOff();
}
