package android.graphics;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class Insets implements Parcelable {
    public final int bottom;
    public final int left;
    public final int right;
    public final int top;
    public static final Insets NONE = new Insets(0, 0, 0, 0);
    public static final Parcelable.Creator<Insets> CREATOR = new Parcelable.Creator<Insets>() { // from class: android.graphics.Insets.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Insets createFromParcel(Parcel in) {
            return new Insets(in.readInt(), in.readInt(), in.readInt(), in.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Insets[] newArray(int size) {
            return new Insets[size];
        }
    };

    private Insets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /* renamed from: of */
    public static Insets m186of(int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            return NONE;
        }
        return new Insets(left, top, right, bottom);
    }

    /* renamed from: of */
    public static Insets m185of(Rect r) {
        return r == null ? NONE : m186of(r.left, r.top, r.right, r.bottom);
    }

    public Rect toRect() {
        return new Rect(this.left, this.top, this.right, this.bottom);
    }

    public static Insets add(Insets a, Insets b) {
        return m186of(a.left + b.left, a.top + b.top, a.right + b.right, a.bottom + b.bottom);
    }

    public static Insets subtract(Insets a, Insets b) {
        return m186of(a.left - b.left, a.top - b.top, a.right - b.right, a.bottom - b.bottom);
    }

    public static Insets max(Insets a, Insets b) {
        return m186of(Math.max(a.left, b.left), Math.max(a.top, b.top), Math.max(a.right, b.right), Math.max(a.bottom, b.bottom));
    }

    public static Insets min(Insets a, Insets b) {
        return m186of(Math.min(a.left, b.left), Math.min(a.top, b.top), Math.min(a.right, b.right), Math.min(a.bottom, b.bottom));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Insets insets = (Insets) o;
        if (this.bottom == insets.bottom && this.left == insets.left && this.right == insets.right && this.top == insets.top) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.left;
        return (((((result * 31) + this.top) * 31) + this.right) * 31) + this.bottom;
    }

    public String toString() {
        return "Insets{left=" + this.left + ", top=" + this.top + ", right=" + this.right + ", bottom=" + this.bottom + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.left);
        out.writeInt(this.top);
        out.writeInt(this.right);
        out.writeInt(this.bottom);
    }
}
