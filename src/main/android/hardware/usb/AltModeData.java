package android.hardware.usb;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public final class AltModeData implements Parcelable {
    public static final Parcelable.Creator<AltModeData> CREATOR = new Parcelable.Creator<AltModeData>() { // from class: android.hardware.usb.AltModeData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AltModeData createFromParcel(Parcel parcel) {
            return new AltModeData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AltModeData[] newArray(int i) {
            return new AltModeData[i];
        }
    };
    public int _tag;
    public Object _value;

    public final int getStability() {
        return 1;
    }

    public AltModeData() {
        this._tag = 0;
        this._value = null;
    }

    public AltModeData(Parcel parcel) {
        readFromParcel(parcel);
    }

    public int getTag() {
        return this._tag;
    }

    public DisplayPortAltModeData getDisplayPortAltModeData() {
        _assertTag(0);
        return (DisplayPortAltModeData) this._value;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._tag);
        if (this._tag != 0) {
            return;
        }
        parcel.writeTypedObject(getDisplayPortAltModeData(), i);
    }

    public void readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        if (readInt == 0) {
            _set(readInt, (DisplayPortAltModeData) parcel.readTypedObject(DisplayPortAltModeData.CREATOR));
            return;
        }
        throw new IllegalArgumentException("union: unknown tag: " + readInt);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        if (getTag() != 0) {
            return 0;
        }
        return 0 | describeContents(getDisplayPortAltModeData());
    }

    public final int describeContents(Object obj) {
        if (obj != null && (obj instanceof Parcelable)) {
            return ((Parcelable) obj).describeContents();
        }
        return 0;
    }

    public final void _assertTag(int i) {
        if (getTag() == i) {
            return;
        }
        throw new IllegalStateException("bad access: " + _tagString(i) + ", " + _tagString(getTag()) + " is available.");
    }

    public final String _tagString(int i) {
        if (i == 0) {
            return "displayPortAltModeData";
        }
        throw new IllegalStateException("unknown field: " + i);
    }

    public final void _set(int i, Object obj) {
        this._tag = i;
        this._value = obj;
    }

    /* loaded from: classes.dex */
    public static class DisplayPortAltModeData implements Parcelable {
        public static final Parcelable.Creator<DisplayPortAltModeData> CREATOR = new Parcelable.Creator<DisplayPortAltModeData>() { // from class: android.hardware.usb.AltModeData.DisplayPortAltModeData.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DisplayPortAltModeData createFromParcel(Parcel parcel) {
                DisplayPortAltModeData displayPortAltModeData = new DisplayPortAltModeData();
                displayPortAltModeData.readFromParcel(parcel);
                return displayPortAltModeData;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DisplayPortAltModeData[] newArray(int i) {
                return new DisplayPortAltModeData[i];
            }
        };
        public int partnerSinkStatus = 0;
        public int cableStatus = 0;
        public int pinAssignment = 0;
        public boolean hpd = false;
        public int linkTrainingStatus = 0;

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
            parcel.writeInt(this.partnerSinkStatus);
            parcel.writeInt(this.cableStatus);
            parcel.writeInt(this.pinAssignment);
            parcel.writeBoolean(this.hpd);
            parcel.writeInt(this.linkTrainingStatus);
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
                    this.partnerSinkStatus = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.cableStatus = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.pinAssignment = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.hpd = parcel.readBoolean();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.linkTrainingStatus = parcel.readInt();
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
}
