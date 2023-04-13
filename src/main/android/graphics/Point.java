package android.graphics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class Point implements Parcelable {
    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() { // from class: android.graphics.Point.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Point createFromParcel(Parcel in) {
            Point r = new Point();
            r.readFromParcel(in);
            return r;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    /* renamed from: x */
    public int f76x;

    /* renamed from: y */
    public int f77y;

    public Point() {
    }

    public Point(int x, int y) {
        this.f76x = x;
        this.f77y = y;
    }

    public Point(Point src) {
        set(src);
    }

    public void set(int x, int y) {
        this.f76x = x;
        this.f77y = y;
    }

    public void set(Point src) {
        this.f76x = src.f76x;
        this.f77y = src.f77y;
    }

    public final void negate() {
        this.f76x = -this.f76x;
        this.f77y = -this.f77y;
    }

    public final void offset(int dx, int dy) {
        this.f76x += dx;
        this.f77y += dy;
    }

    public final boolean equals(int x, int y) {
        return this.f76x == x && this.f77y == y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        if (this.f76x == point.f76x && this.f77y == point.f77y) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.f76x;
        return (result * 31) + this.f77y;
    }

    public String toString() {
        return "Point(" + this.f76x + ", " + this.f77y + NavigationBarInflaterView.KEY_CODE_END;
    }

    public String flattenToString() {
        return this.f76x + "x" + this.f77y;
    }

    public static Point unflattenFromString(String s) throws NumberFormatException {
        int sep_ix = s.indexOf("x");
        return new Point(Integer.parseInt(s.substring(0, sep_ix)), Integer.parseInt(s.substring(sep_ix + 1)));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.f76x);
        out.writeInt(this.f77y);
    }

    public void readFromParcel(Parcel in) {
        this.f76x = in.readInt();
        this.f77y = in.readInt();
    }
}
