package android.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.AttributeSet;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class ArcMotion extends PathMotion {
    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70.0f;
    private static final float DEFAULT_MAX_TANGENT = (float) Math.tan(Math.toRadians(35.0d));
    private static final float DEFAULT_MIN_ANGLE_DEGREES = 0.0f;
    private float mMaximumAngle;
    private float mMaximumTangent;
    private float mMinimumHorizontalAngle;
    private float mMinimumHorizontalTangent;
    private float mMinimumVerticalAngle;
    private float mMinimumVerticalTangent;

    public ArcMotion() {
        this.mMinimumHorizontalAngle = 0.0f;
        this.mMinimumVerticalAngle = 0.0f;
        this.mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
        this.mMinimumHorizontalTangent = 0.0f;
        this.mMinimumVerticalTangent = 0.0f;
        this.mMaximumTangent = DEFAULT_MAX_TANGENT;
    }

    public ArcMotion(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMinimumHorizontalAngle = 0.0f;
        this.mMinimumVerticalAngle = 0.0f;
        this.mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
        this.mMinimumHorizontalTangent = 0.0f;
        this.mMinimumVerticalTangent = 0.0f;
        this.mMaximumTangent = DEFAULT_MAX_TANGENT;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ArcMotion);
        float minimumVerticalAngle = a.getFloat(1, 0.0f);
        setMinimumVerticalAngle(minimumVerticalAngle);
        float minimumHorizontalAngle = a.getFloat(0, 0.0f);
        setMinimumHorizontalAngle(minimumHorizontalAngle);
        float maximumAngle = a.getFloat(2, DEFAULT_MAX_ANGLE_DEGREES);
        setMaximumAngle(maximumAngle);
        a.recycle();
    }

    public void setMinimumHorizontalAngle(float angleInDegrees) {
        this.mMinimumHorizontalAngle = angleInDegrees;
        this.mMinimumHorizontalTangent = toTangent(angleInDegrees);
    }

    public float getMinimumHorizontalAngle() {
        return this.mMinimumHorizontalAngle;
    }

    public void setMinimumVerticalAngle(float angleInDegrees) {
        this.mMinimumVerticalAngle = angleInDegrees;
        this.mMinimumVerticalTangent = toTangent(angleInDegrees);
    }

    public float getMinimumVerticalAngle() {
        return this.mMinimumVerticalAngle;
    }

    public void setMaximumAngle(float angleInDegrees) {
        this.mMaximumAngle = angleInDegrees;
        this.mMaximumTangent = toTangent(angleInDegrees);
    }

    public float getMaximumAngle() {
        return this.mMaximumAngle;
    }

    private static float toTangent(float arcInDegrees) {
        if (arcInDegrees < 0.0f || arcInDegrees > 90.0f) {
            throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
        }
        return (float) Math.tan(Math.toRadians(arcInDegrees / 2.0f));
    }

    @Override // android.transition.PathMotion
    public Path getPath(float startX, float startY, float endX, float endY) {
        float ex;
        float ey;
        float minimumArcDist2;
        float eDistX;
        float ey2;
        float ex2;
        float newArcDistance2;
        float ex3;
        float ey3;
        Path path = new Path();
        path.moveTo(startX, startY);
        float deltaX = endX - startX;
        float deltaY = endY - startY;
        float h2 = (deltaX * deltaX) + (deltaY * deltaY);
        float dx = (startX + endX) / 2.0f;
        float dy = (startY + endY) / 2.0f;
        float midDist2 = h2 * 0.25f;
        boolean isMovingUpwards = startY > endY;
        if (deltaY == 0.0f) {
            eDistX = dx;
            ey = (Math.abs(deltaX) * 0.5f * this.mMinimumHorizontalTangent) + dy;
            minimumArcDist2 = 0.0f;
        } else if (deltaX == 0.0f) {
            eDistX = (Math.abs(deltaY) * 0.5f * this.mMinimumVerticalTangent) + dx;
            ey = dy;
            minimumArcDist2 = 0.0f;
        } else {
            float ex4 = Math.abs(deltaX);
            if (ex4 < Math.abs(deltaY)) {
                float eDistY = Math.abs(h2 / (deltaY * 2.0f));
                if (isMovingUpwards) {
                    ey2 = endY + eDistY;
                    ex2 = endX;
                } else {
                    ey2 = startY + eDistY;
                    ex2 = startX;
                }
                float f = this.mMinimumVerticalTangent;
                float minimumArcDist22 = midDist2 * f * f;
                minimumArcDist2 = minimumArcDist22;
                eDistX = ex2;
                ey = ey2;
            } else {
                float eDistX2 = h2 / (deltaX * 2.0f);
                if (isMovingUpwards) {
                    ex = startX + eDistX2;
                    ey = startY;
                } else {
                    ex = endX - eDistX2;
                    ey = endY;
                }
                float f2 = this.mMinimumHorizontalTangent;
                float minimumArcDist23 = midDist2 * f2 * f2;
                minimumArcDist2 = minimumArcDist23;
                eDistX = ex;
            }
        }
        float arcDistX = dx - eDistX;
        float arcDistY = dy - ey;
        float arcDist2 = (arcDistX * arcDistX) + (arcDistY * arcDistY);
        float f3 = this.mMaximumTangent;
        float maximumArcDist2 = midDist2 * f3 * f3;
        if (arcDist2 != 0.0f && arcDist2 < minimumArcDist2) {
            float newArcDistance22 = minimumArcDist2;
            newArcDistance2 = newArcDistance22;
        } else if (arcDist2 <= maximumArcDist2) {
            newArcDistance2 = 0.0f;
        } else {
            newArcDistance2 = maximumArcDist2;
        }
        if (newArcDistance2 == 0.0f) {
            ex3 = eDistX;
            ey3 = ey;
        } else {
            float ratio2 = newArcDistance2 / arcDist2;
            float ratio = (float) Math.sqrt(ratio2);
            float ex5 = dx + ((eDistX - dx) * ratio);
            ex3 = ex5;
            ey3 = dy + ((ey - dy) * ratio);
        }
        float control1X = (startX + ex3) / 2.0f;
        float control1Y = (startY + ey3) / 2.0f;
        float control2X = (ex3 + endX) / 2.0f;
        float control2Y = (ey3 + endY) / 2.0f;
        path.cubicTo(control1X, control1Y, control2X, control2Y, endX, endY);
        return path;
    }
}
