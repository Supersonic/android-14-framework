package android.filterfw.geometry;
/* loaded from: classes.dex */
public class Rectangle extends Quad {
    public Rectangle() {
    }

    public Rectangle(float x, float y, float width, float height) {
        super(new Point(x, y), new Point(x + width, y), new Point(x, y + height), new Point(x + width, y + height));
    }

    public Rectangle(Point origin, Point size) {
        super(origin, origin.plus(size.f50x, 0.0f), origin.plus(0.0f, size.f51y), origin.plus(size.f50x, size.f51y));
    }

    public static Rectangle fromRotatedRect(Point center, Point size, float rotation) {
        Point p0 = new Point(center.f50x - (size.f50x / 2.0f), center.f51y - (size.f51y / 2.0f));
        Point p1 = new Point(center.f50x + (size.f50x / 2.0f), center.f51y - (size.f51y / 2.0f));
        Point p2 = new Point(center.f50x - (size.f50x / 2.0f), center.f51y + (size.f51y / 2.0f));
        Point p3 = new Point(center.f50x + (size.f50x / 2.0f), center.f51y + (size.f51y / 2.0f));
        return new Rectangle(p0.rotatedAround(center, rotation), p1.rotatedAround(center, rotation), p2.rotatedAround(center, rotation), p3.rotatedAround(center, rotation));
    }

    private Rectangle(Point p0, Point p1, Point p2, Point p3) {
        super(p0, p1, p2, p3);
    }

    public static Rectangle fromCenterVerticalAxis(Point center, Point vAxis, Point size) {
        Point dy = vAxis.scaledTo(size.f51y / 2.0f);
        Point dx = vAxis.rotated90(1).scaledTo(size.f50x / 2.0f);
        return new Rectangle(center.minus(dx).minus(dy), center.plus(dx).minus(dy), center.minus(dx).plus(dy), center.plus(dx).plus(dy));
    }

    public float getWidth() {
        return this.f53p1.minus(this.f52p0).length();
    }

    public float getHeight() {
        return this.f54p2.minus(this.f52p0).length();
    }

    public Point center() {
        return this.f52p0.plus(this.f53p1).plus(this.f54p2).plus(this.f55p3).times(0.25f);
    }

    @Override // android.filterfw.geometry.Quad
    public Rectangle scaled(float s) {
        return new Rectangle(this.f52p0.times(s), this.f53p1.times(s), this.f54p2.times(s), this.f55p3.times(s));
    }

    @Override // android.filterfw.geometry.Quad
    public Rectangle scaled(float x, float y) {
        return new Rectangle(this.f52p0.mult(x, y), this.f53p1.mult(x, y), this.f54p2.mult(x, y), this.f55p3.mult(x, y));
    }
}
