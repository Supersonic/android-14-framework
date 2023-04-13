package android.filterfw.geometry;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes.dex */
public class Point {

    /* renamed from: x */
    public float f50x;

    /* renamed from: y */
    public float f51y;

    public Point() {
    }

    public Point(float x, float y) {
        this.f50x = x;
        this.f51y = y;
    }

    public void set(float x, float y) {
        this.f50x = x;
        this.f51y = y;
    }

    public boolean IsInUnitRange() {
        float f = this.f50x;
        if (f >= 0.0f && f <= 1.0f) {
            float f2 = this.f51y;
            if (f2 >= 0.0f && f2 <= 1.0f) {
                return true;
            }
        }
        return false;
    }

    public Point plus(float x, float y) {
        return new Point(this.f50x + x, this.f51y + y);
    }

    public Point plus(Point point) {
        return plus(point.f50x, point.f51y);
    }

    public Point minus(float x, float y) {
        return new Point(this.f50x - x, this.f51y - y);
    }

    public Point minus(Point point) {
        return minus(point.f50x, point.f51y);
    }

    public Point times(float s) {
        return new Point(this.f50x * s, this.f51y * s);
    }

    public Point mult(float x, float y) {
        return new Point(this.f50x * x, this.f51y * y);
    }

    public float length() {
        return (float) Math.hypot(this.f50x, this.f51y);
    }

    public float distanceTo(Point p) {
        return p.minus(this).length();
    }

    public Point scaledTo(float length) {
        return times(length / length());
    }

    public Point normalize() {
        return scaledTo(1.0f);
    }

    public Point rotated90(int count) {
        float nx = this.f50x;
        float ny = this.f51y;
        for (int i = 0; i < count; i++) {
            float ox = nx;
            nx = ny;
            ny = -ox;
        }
        return new Point(nx, ny);
    }

    public Point rotated(float radians) {
        return new Point((float) ((Math.cos(radians) * this.f50x) - (Math.sin(radians) * this.f51y)), (float) ((Math.sin(radians) * this.f50x) + (Math.cos(radians) * this.f51y)));
    }

    public Point rotatedAround(Point center, float radians) {
        return minus(center).rotated(radians).plus(center);
    }

    public String toString() {
        return NavigationBarInflaterView.KEY_CODE_START + this.f50x + ", " + this.f51y + NavigationBarInflaterView.KEY_CODE_END;
    }
}
