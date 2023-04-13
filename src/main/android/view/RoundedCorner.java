package android.view;

import android.graphics.Point;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class RoundedCorner implements Parcelable {
    public static final Parcelable.Creator<RoundedCorner> CREATOR = new Parcelable.Creator<RoundedCorner>() { // from class: android.view.RoundedCorner.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoundedCorner createFromParcel(Parcel in) {
            return new RoundedCorner(in.readInt(), in.readInt(), in.readInt(), in.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RoundedCorner[] newArray(int size) {
            return new RoundedCorner[size];
        }
    };
    public static final int POSITION_BOTTOM_LEFT = 3;
    public static final int POSITION_BOTTOM_RIGHT = 2;
    public static final int POSITION_TOP_LEFT = 0;
    public static final int POSITION_TOP_RIGHT = 1;
    private final Point mCenter;
    private final int mPosition;
    private final int mRadius;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Position {
    }

    public RoundedCorner(int position) {
        this.mPosition = position;
        this.mRadius = 0;
        this.mCenter = new Point(0, 0);
    }

    public RoundedCorner(int position, int radius, int centerX, int centerY) {
        this.mPosition = position;
        this.mRadius = radius;
        this.mCenter = new Point(centerX, centerY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RoundedCorner(RoundedCorner rc) {
        this.mPosition = rc.getPosition();
        this.mRadius = rc.getRadius();
        this.mCenter = new Point(rc.getCenter());
    }

    public int getPosition() {
        return this.mPosition;
    }

    public int getRadius() {
        return this.mRadius;
    }

    public Point getCenter() {
        return new Point(this.mCenter);
    }

    public boolean isEmpty() {
        return this.mRadius == 0 || this.mCenter.f76x <= 0 || this.mCenter.f77y <= 0;
    }

    private String getPositionString(int position) {
        switch (position) {
            case 0:
                return "TopLeft";
            case 1:
                return "TopRight";
            case 2:
                return "BottomRight";
            case 3:
                return "BottomLeft";
            default:
                return "Invalid";
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoundedCorner) {
            RoundedCorner r = (RoundedCorner) o;
            return this.mPosition == r.mPosition && this.mRadius == r.mRadius && this.mCenter.equals(r.mCenter);
        }
        return false;
    }

    public int hashCode() {
        int result = (0 * 31) + this.mPosition;
        return (((result * 31) + this.mRadius) * 31) + this.mCenter.hashCode();
    }

    public String toString() {
        return "RoundedCorner{position=" + getPositionString(this.mPosition) + ", radius=" + this.mRadius + ", center=" + this.mCenter + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mPosition);
        out.writeInt(this.mRadius);
        out.writeInt(this.mCenter.f76x);
        out.writeInt(this.mCenter.f77y);
    }
}
