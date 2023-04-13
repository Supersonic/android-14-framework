package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.Objects;
/* loaded from: classes.dex */
public class RectShape extends Shape {
    private RectF mRect = new RectF();

    @Override // android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(this.mRect, paint);
    }

    @Override // android.graphics.drawable.shapes.Shape
    public void getOutline(Outline outline) {
        RectF rect = rect();
        outline.setRect((int) Math.ceil(rect.left), (int) Math.ceil(rect.top), (int) Math.floor(rect.right), (int) Math.floor(rect.bottom));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.shapes.Shape
    public void onResize(float width, float height) {
        this.mRect.set(0.0f, 0.0f, width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RectF rect() {
        return this.mRect;
    }

    @Override // android.graphics.drawable.shapes.Shape
    /* renamed from: clone */
    public RectShape mo1394clone() throws CloneNotSupportedException {
        RectShape shape = (RectShape) super.mo1394clone();
        shape.mRect = new RectF(this.mRect);
        return shape;
    }

    @Override // android.graphics.drawable.shapes.Shape
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        RectShape rectShape = (RectShape) o;
        return Objects.equals(this.mRect, rectShape.mRect);
    }

    @Override // android.graphics.drawable.shapes.Shape
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mRect);
    }
}
