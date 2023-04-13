package android.filterfw.geometry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class Quad {

    /* renamed from: p0 */
    public Point f52p0;

    /* renamed from: p1 */
    public Point f53p1;

    /* renamed from: p2 */
    public Point f54p2;

    /* renamed from: p3 */
    public Point f55p3;

    public Quad() {
    }

    public Quad(Point p0, Point p1, Point p2, Point p3) {
        this.f52p0 = p0;
        this.f53p1 = p1;
        this.f54p2 = p2;
        this.f55p3 = p3;
    }

    public boolean IsInUnitRange() {
        return this.f52p0.IsInUnitRange() && this.f53p1.IsInUnitRange() && this.f54p2.IsInUnitRange() && this.f55p3.IsInUnitRange();
    }

    public Quad translated(Point t) {
        return new Quad(this.f52p0.plus(t), this.f53p1.plus(t), this.f54p2.plus(t), this.f55p3.plus(t));
    }

    public Quad translated(float x, float y) {
        return new Quad(this.f52p0.plus(x, y), this.f53p1.plus(x, y), this.f54p2.plus(x, y), this.f55p3.plus(x, y));
    }

    public Quad scaled(float s) {
        return new Quad(this.f52p0.times(s), this.f53p1.times(s), this.f54p2.times(s), this.f55p3.times(s));
    }

    public Quad scaled(float x, float y) {
        return new Quad(this.f52p0.mult(x, y), this.f53p1.mult(x, y), this.f54p2.mult(x, y), this.f55p3.mult(x, y));
    }

    public Rectangle boundingBox() {
        List<Float> xs = Arrays.asList(Float.valueOf(this.f52p0.f50x), Float.valueOf(this.f53p1.f50x), Float.valueOf(this.f54p2.f50x), Float.valueOf(this.f55p3.f50x));
        List<Float> ys = Arrays.asList(Float.valueOf(this.f52p0.f51y), Float.valueOf(this.f53p1.f51y), Float.valueOf(this.f54p2.f51y), Float.valueOf(this.f55p3.f51y));
        float x0 = ((Float) Collections.min(xs)).floatValue();
        float y0 = ((Float) Collections.min(ys)).floatValue();
        float x1 = ((Float) Collections.max(xs)).floatValue();
        float y1 = ((Float) Collections.max(ys)).floatValue();
        return new Rectangle(x0, y0, x1 - x0, y1 - y0);
    }

    public float getBoundingWidth() {
        List<Float> xs = Arrays.asList(Float.valueOf(this.f52p0.f50x), Float.valueOf(this.f53p1.f50x), Float.valueOf(this.f54p2.f50x), Float.valueOf(this.f55p3.f50x));
        return ((Float) Collections.max(xs)).floatValue() - ((Float) Collections.min(xs)).floatValue();
    }

    public float getBoundingHeight() {
        List<Float> ys = Arrays.asList(Float.valueOf(this.f52p0.f51y), Float.valueOf(this.f53p1.f51y), Float.valueOf(this.f54p2.f51y), Float.valueOf(this.f55p3.f51y));
        return ((Float) Collections.max(ys)).floatValue() - ((Float) Collections.min(ys)).floatValue();
    }

    public String toString() {
        return "{" + this.f52p0 + ", " + this.f53p1 + ", " + this.f54p2 + ", " + this.f55p3 + "}";
    }
}
