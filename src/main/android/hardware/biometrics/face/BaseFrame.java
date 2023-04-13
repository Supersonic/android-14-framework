package android.hardware.biometrics.face;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class BaseFrame implements Parcelable {
    public static final Parcelable.Creator<BaseFrame> CREATOR = new Parcelable.Creator<BaseFrame>() { // from class: android.hardware.biometrics.face.BaseFrame.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BaseFrame createFromParcel(Parcel parcel) {
            BaseFrame baseFrame = new BaseFrame();
            baseFrame.readFromParcel(parcel);
            return baseFrame;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BaseFrame[] newArray(int i) {
            return new BaseFrame[i];
        }
    };
    public byte acquiredInfo = 0;
    public int vendorCode = 0;
    public float pan = 0.0f;
    public float tilt = 0.0f;
    public float distance = 0.0f;
    public boolean isCancellable = false;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeByte(this.acquiredInfo);
        parcel.writeInt(this.vendorCode);
        parcel.writeFloat(this.pan);
        parcel.writeFloat(this.tilt);
        parcel.writeFloat(this.distance);
        parcel.writeBoolean(this.isCancellable);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
            if (readInt < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (parcel.dataPosition() - dataPosition < readInt) {
                this.acquiredInfo = parcel.readByte();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.vendorCode = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.pan = parcel.readFloat();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.tilt = parcel.readFloat();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.distance = parcel.readFloat();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.isCancellable = parcel.readBoolean();
                                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                                        throw new BadParcelableException("Overflow in the size of parcelable");
                                    }
                                    parcel.setDataPosition(dataPosition + readInt);
                                    return;
                                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                    throw new BadParcelableException("Overflow in the size of parcelable");
                                }
                            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                throw new BadParcelableException("Overflow in the size of parcelable");
                            }
                        } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
        } catch (Throwable th) {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            throw th;
        }
    }
}
