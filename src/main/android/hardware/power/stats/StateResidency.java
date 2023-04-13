package android.hardware.power.stats;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class StateResidency implements Parcelable {
    public static final Parcelable.Creator<StateResidency> CREATOR = new Parcelable.Creator<StateResidency>() { // from class: android.hardware.power.stats.StateResidency.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StateResidency createFromParcel(Parcel parcel) {
            StateResidency stateResidency = new StateResidency();
            stateResidency.readFromParcel(parcel);
            return stateResidency;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StateResidency[] newArray(int i) {
            return new StateResidency[i];
        }
    };

    /* renamed from: id */
    public int f10id = 0;
    public long totalTimeInStateMs = 0;
    public long totalStateEntryCount = 0;
    public long lastEntryTimestampMs = 0;

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
        parcel.writeInt(this.f10id);
        parcel.writeLong(this.totalTimeInStateMs);
        parcel.writeLong(this.totalStateEntryCount);
        parcel.writeLong(this.lastEntryTimestampMs);
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
                this.f10id = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.totalTimeInStateMs = parcel.readLong();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.totalStateEntryCount = parcel.readLong();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.lastEntryTimestampMs = parcel.readLong();
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
