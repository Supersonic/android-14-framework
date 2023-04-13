package android.graphics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class PointF implements Parcelable {
    public static final Parcelable.Creator<PointF> CREATOR = new Parcelable.Creator<PointF>() { // from class: android.graphics.PointF.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointF createFromParcel(Parcel in) {
            PointF r = new PointF();
            r.readFromParcel(in);
            return r;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointF[] newArray(int size) {
            return new PointF[size];
        }
    };

    /* renamed from: x */
    public float f78x;

    /* renamed from: y */
    public float f79y;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.f78x = x;
        this.f79y = y;
    }

    public PointF(Point p) {
        this.f78x = p.f76x;
        this.f79y = p.f77y;
    }

    public PointF(PointF p) {
        this.f78x = p.f78x;
        this.f79y = p.f79y;
    }

    public final void set(float x, float y) {
        this.f78x = x;
        this.f79y = y;
    }

    public final void set(PointF p) {
        this.f78x = p.f78x;
        this.f79y = p.f79y;
    }

    public final void negate() {
        this.f78x = -this.f78x;
        this.f79y = -this.f79y;
    }

    public final void offset(float dx, float dy) {
        this.f78x += dx;
        this.f79y += dy;
    }

    public final boolean equals(float x, float y) {
        return this.f78x == x && this.f79y == y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointF pointF = (PointF) o;
        if (Float.compare(pointF.f78x, this.f78x) == 0 && Float.compare(pointF.f79y, this.f79y) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        float f = this.f78x;
        int result = f != 0.0f ? Float.floatToIntBits(f) : 0;
        int i = result * 31;
        float f2 = this.f79y;
        int result2 = i + (f2 != 0.0f ? Float.floatToIntBits(f2) : 0);
        return result2;
    }

    public String toString() {
        return "PointF(" + this.f78x + ", " + this.f79y + NavigationBarInflaterView.KEY_CODE_END;
    }

    public final float length() {
        return length(this.f78x, this.f79y);
    }

    public static float length(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.f78x);
        out.writeFloat(this.f79y);
    }

    public void readFromParcel(Parcel in) {
        this.f78x = in.readFloat();
        this.f79y = in.readFloat();
    }
}
