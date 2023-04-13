package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import com.android.internal.C4057R;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class PaintDrawable extends ShapeDrawable {
    public PaintDrawable() {
    }

    public PaintDrawable(int color) {
        getPaint().setColor(color);
    }

    public void setCornerRadius(float radius) {
        float[] radii = null;
        if (radius > 0.0f) {
            radii = new float[8];
            for (int i = 0; i < 8; i++) {
                radii[i] = radius;
            }
        }
        setCornerRadii(radii);
    }

    public void setCornerRadii(float[] radii) {
        if (radii == null) {
            if (getShape() != null) {
                setShape(null);
            }
        } else {
            setShape(new RoundRectShape(radii, null, null));
        }
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.ShapeDrawable
    public boolean inflateTag(String name, Resources r, XmlPullParser parser, AttributeSet attrs) {
        if (name.equals("corners")) {
            TypedArray a = r.obtainAttributes(attrs, C4057R.styleable.DrawableCorners);
            int radius = a.getDimensionPixelSize(0, 0);
            setCornerRadius(radius);
            int topLeftRadius = a.getDimensionPixelSize(1, radius);
            int topRightRadius = a.getDimensionPixelSize(2, radius);
            int bottomLeftRadius = a.getDimensionPixelSize(3, radius);
            int bottomRightRadius = a.getDimensionPixelSize(4, radius);
            if (topLeftRadius != radius || topRightRadius != radius || bottomLeftRadius != radius || bottomRightRadius != radius) {
                setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius});
            }
            a.recycle();
            return true;
        }
        return super.inflateTag(name, r, parser, attrs);
    }
}
