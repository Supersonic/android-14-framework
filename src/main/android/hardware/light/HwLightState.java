package android.hardware.light;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class HwLightState implements Parcelable {
    public static final Parcelable.Creator<HwLightState> CREATOR = new Parcelable.Creator<HwLightState>() { // from class: android.hardware.light.HwLightState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HwLightState createFromParcel(Parcel parcel) {
            HwLightState hwLightState = new HwLightState();
            hwLightState.readFromParcel(parcel);
            return hwLightState;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HwLightState[] newArray(int i) {
            return new HwLightState[i];
        }
    };
    public byte brightnessMode;
    public byte flashMode;
    public int color = 0;
    public int flashOnMs = 0;
    public int flashOffMs = 0;

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
        parcel.writeInt(this.color);
        parcel.writeByte(this.flashMode);
        parcel.writeInt(this.flashOnMs);
        parcel.writeInt(this.flashOffMs);
        parcel.writeByte(this.brightnessMode);
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
                this.color = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.flashMode = parcel.readByte();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.flashOnMs = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.flashOffMs = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.brightnessMode = parcel.readByte();
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
