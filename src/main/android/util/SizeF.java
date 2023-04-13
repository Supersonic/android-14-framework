package android.util;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public final class SizeF implements Parcelable {
    public static final Parcelable.Creator<SizeF> CREATOR = new Parcelable.Creator<SizeF>() { // from class: android.util.SizeF.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SizeF createFromParcel(Parcel in) {
            float width = in.readFloat();
            float height = in.readFloat();
            return new SizeF(width, height);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SizeF[] newArray(int size) {
            return new SizeF[size];
        }
    };
    private final float mHeight;
    private final float mWidth;

    public SizeF(float width, float height) {
        this.mWidth = Preconditions.checkArgumentFinite(width, "width");
        this.mHeight = Preconditions.checkArgumentFinite(height, "height");
    }

    public float getWidth() {
        return this.mWidth;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SizeF)) {
            return false;
        }
        SizeF other = (SizeF) obj;
        if (this.mWidth != other.mWidth || this.mHeight != other.mHeight) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.mWidth + "x" + this.mHeight;
    }

    private static NumberFormatException invalidSizeF(String s) {
        throw new NumberFormatException("Invalid SizeF: \"" + s + "\"");
    }

    public static SizeF parseSizeF(String string) throws NumberFormatException {
        Preconditions.checkNotNull(string, "string must not be null");
        int sep_ix = string.indexOf(42);
        if (sep_ix < 0) {
            sep_ix = string.indexOf(120);
        }
        if (sep_ix < 0) {
            NumberFormatException e = invalidSizeF(string);
            throw e;
        }
        try {
            return new SizeF(Float.parseFloat(string.substring(0, sep_ix)), Float.parseFloat(string.substring(sep_ix + 1)));
        } catch (NumberFormatException e2) {
            throw invalidSizeF(string);
        } catch (IllegalArgumentException e3) {
            throw invalidSizeF(string);
        }
    }

    public int hashCode() {
        return Float.floatToIntBits(this.mWidth) ^ Float.floatToIntBits(this.mHeight);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.mWidth);
        out.writeFloat(this.mHeight);
    }
}
