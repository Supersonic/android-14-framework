package android.service.displayhash;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Size;
@SystemApi
/* loaded from: classes3.dex */
public final class DisplayHashParams implements Parcelable {
    public static final Parcelable.Creator<DisplayHashParams> CREATOR = new Parcelable.Creator<DisplayHashParams>() { // from class: android.service.displayhash.DisplayHashParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayHashParams[] newArray(int size) {
            return new DisplayHashParams[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayHashParams createFromParcel(Parcel in) {
            return new DisplayHashParams(in);
        }
    };
    private final Size mBufferSize;
    private final boolean mGrayscaleBuffer;

    /* loaded from: classes3.dex */
    public static final class Builder {
        private Size mBufferSize;
        private boolean mGrayscaleBuffer;

        public Builder setBufferSize(int width, int height) {
            this.mBufferSize = new Size(width, height);
            return this;
        }

        public Builder setGrayscaleBuffer(boolean grayscaleBuffer) {
            this.mGrayscaleBuffer = grayscaleBuffer;
            return this;
        }

        public DisplayHashParams build() {
            return new DisplayHashParams(this.mBufferSize, this.mGrayscaleBuffer);
        }
    }

    public DisplayHashParams(Size bufferSize, boolean grayscaleBuffer) {
        this.mBufferSize = bufferSize;
        this.mGrayscaleBuffer = grayscaleBuffer;
    }

    public Size getBufferSize() {
        return this.mBufferSize;
    }

    public boolean isGrayscaleBuffer() {
        return this.mGrayscaleBuffer;
    }

    public String toString() {
        return "DisplayHashParams { bufferSize = " + this.mBufferSize + ", grayscaleBuffer = " + this.mGrayscaleBuffer + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mGrayscaleBuffer ? (byte) (0 | 2) : (byte) 0;
        if (this.mBufferSize != null) {
            flg = (byte) (flg | 1);
        }
        dest.writeByte(flg);
        Size size = this.mBufferSize;
        if (size != null) {
            dest.writeSize(size);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    DisplayHashParams(Parcel in) {
        byte flg = in.readByte();
        boolean grayscaleBuffer = (flg & 2) != 0;
        Size bufferSize = (flg & 1) == 0 ? null : in.readSize();
        this.mBufferSize = bufferSize;
        this.mGrayscaleBuffer = grayscaleBuffer;
    }

    @Deprecated
    private void __metadata() {
    }
}
