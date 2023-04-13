package android.view;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
/* loaded from: classes4.dex */
public class PrivacyIndicatorBounds implements Parcelable {
    public static final Parcelable.Creator<PrivacyIndicatorBounds> CREATOR = new Parcelable.Creator<PrivacyIndicatorBounds>() { // from class: android.view.PrivacyIndicatorBounds.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrivacyIndicatorBounds[] newArray(int size) {
            return new PrivacyIndicatorBounds[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrivacyIndicatorBounds createFromParcel(Parcel in) {
            return new PrivacyIndicatorBounds(in);
        }
    };
    private final int mRotation;
    private final Rect[] mStaticBounds;

    public PrivacyIndicatorBounds() {
        this.mStaticBounds = new Rect[4];
        this.mRotation = 0;
    }

    public PrivacyIndicatorBounds(Rect[] staticBounds, int rotation) {
        this.mStaticBounds = staticBounds;
        this.mRotation = rotation;
    }

    public PrivacyIndicatorBounds updateStaticBounds(Rect[] staticPositions) {
        return new PrivacyIndicatorBounds(staticPositions, this.mRotation);
    }

    public PrivacyIndicatorBounds updateBoundsForRotation(Rect bounds, int rotation) {
        Rect[] rectArr = this.mStaticBounds;
        if (rotation >= rectArr.length || rotation < 0) {
            return this;
        }
        Rect[] newBounds = (Rect[]) ArrayUtils.cloneOrNull(rectArr);
        newBounds[rotation] = bounds;
        return updateStaticBounds(newBounds);
    }

    public PrivacyIndicatorBounds inset(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        if (insetLeft == 0 && insetTop == 0 && insetRight == 0 && insetBottom == 0) {
            return this;
        }
        Rect[] insetStaticBounds = new Rect[this.mStaticBounds.length];
        int i = 0;
        while (true) {
            Rect[] rectArr = this.mStaticBounds;
            if (i < rectArr.length) {
                insetStaticBounds[i] = insetRect(rectArr[i], insetLeft, insetTop, insetRight, insetBottom);
                i++;
            } else {
                return updateStaticBounds(insetStaticBounds);
            }
        }
    }

    private static Rect insetRect(Rect orig, int insetLeft, int insetTop, int insetRight, int insetBottom) {
        if (orig == null) {
            return null;
        }
        int left = Math.max(0, orig.left - insetLeft);
        int top = Math.max(0, orig.top - insetTop);
        int right = Math.max(left, orig.right - insetRight);
        int bottom = Math.max(top, orig.bottom - insetBottom);
        return new Rect(left, top, right, bottom);
    }

    public PrivacyIndicatorBounds rotate(int rotation) {
        if (rotation == 0) {
            return this;
        }
        return new PrivacyIndicatorBounds(this.mStaticBounds, rotation);
    }

    public PrivacyIndicatorBounds scale(float scale) {
        if (scale == 1.0f) {
            return this;
        }
        Rect[] scaledStaticPos = new Rect[this.mStaticBounds.length];
        int i = 0;
        while (true) {
            Rect[] rectArr = this.mStaticBounds;
            if (i < rectArr.length) {
                scaledStaticPos[i] = scaleRect(rectArr[i], scale);
                i++;
            } else {
                return new PrivacyIndicatorBounds(scaledStaticPos, this.mRotation);
            }
        }
    }

    private static Rect scaleRect(Rect orig, float scale) {
        if (orig == null) {
            return null;
        }
        Rect newRect = new Rect(orig);
        newRect.scale(scale);
        return newRect;
    }

    public Rect getStaticPrivacyIndicatorBounds() {
        return this.mStaticBounds[this.mRotation];
    }

    public String toString() {
        return "PrivacyIndicatorBounds {static bounds=" + getStaticPrivacyIndicatorBounds() + " rotation=" + this.mRotation + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrivacyIndicatorBounds that = (PrivacyIndicatorBounds) o;
        if (Arrays.equals(this.mStaticBounds, that.mStaticBounds) && this.mRotation == that.mRotation) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Arrays.hashCode(this.mStaticBounds);
        return (_hash * 31) + this.mRotation;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.mStaticBounds, flags);
        dest.writeInt(this.mRotation);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected PrivacyIndicatorBounds(Parcel in) {
        Rect[] staticBounds = (Rect[]) in.createTypedArray(Rect.CREATOR);
        int rotation = in.readInt();
        this.mStaticBounds = staticBounds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) staticBounds);
        this.mRotation = rotation;
    }

    @Deprecated
    private void __metadata() {
    }
}
