package android.frameworks.location.altitude;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class AddMslAltitudeToLocationRequest implements Parcelable {
    public static final Parcelable.Creator<AddMslAltitudeToLocationRequest> CREATOR = new Parcelable.Creator<AddMslAltitudeToLocationRequest>() { // from class: android.frameworks.location.altitude.AddMslAltitudeToLocationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AddMslAltitudeToLocationRequest createFromParcel(Parcel parcel) {
            AddMslAltitudeToLocationRequest addMslAltitudeToLocationRequest = new AddMslAltitudeToLocationRequest();
            addMslAltitudeToLocationRequest.readFromParcel(parcel);
            return addMslAltitudeToLocationRequest;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AddMslAltitudeToLocationRequest[] newArray(int i) {
            return new AddMslAltitudeToLocationRequest[i];
        }
    };
    public double latitudeDegrees = 0.0d;
    public double longitudeDegrees = 0.0d;
    public double altitudeMeters = 0.0d;
    public float verticalAccuracyMeters = 0.0f;

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
        parcel.writeDouble(this.latitudeDegrees);
        parcel.writeDouble(this.longitudeDegrees);
        parcel.writeDouble(this.altitudeMeters);
        parcel.writeFloat(this.verticalAccuracyMeters);
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
                this.latitudeDegrees = parcel.readDouble();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.longitudeDegrees = parcel.readDouble();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.altitudeMeters = parcel.readDouble();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.verticalAccuracyMeters = parcel.readFloat();
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
