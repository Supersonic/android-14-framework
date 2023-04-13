package android.hardware.display;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class Curve implements Parcelable {
    public static final Parcelable.Creator<Curve> CREATOR = new Parcelable.Creator<Curve>() { // from class: android.hardware.display.Curve.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Curve createFromParcel(Parcel in) {
            float[] x = in.createFloatArray();
            float[] y = in.createFloatArray();
            return new Curve(x, y);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Curve[] newArray(int size) {
            return new Curve[size];
        }
    };

    /* renamed from: mX */
    private final float[] f126mX;

    /* renamed from: mY */
    private final float[] f127mY;

    public Curve(float[] x, float[] y) {
        this.f126mX = x;
        this.f127mY = y;
    }

    public float[] getX() {
        return this.f126mX;
    }

    public float[] getY() {
        return this.f127mY;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloatArray(this.f126mX);
        out.writeFloatArray(this.f127mY);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(NavigationBarInflaterView.SIZE_MOD_START);
        int size = this.f126mX.length;
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(NavigationBarInflaterView.KEY_CODE_START).append(this.f126mX[i]).append(", ").append(this.f127mY[i]).append(NavigationBarInflaterView.KEY_CODE_END);
        }
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }
}
