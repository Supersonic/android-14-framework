package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class Properties implements Parcelable {
    public static final Parcelable.Creator<Properties> CREATOR = new Parcelable.Creator<Properties>() { // from class: android.hardware.broadcastradio.Properties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Properties createFromParcel(Parcel parcel) {
            Properties properties = new Properties();
            properties.readFromParcel(parcel);
            return properties;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Properties[] newArray(int i) {
            return new Properties[i];
        }
    };
    public String maker;
    public String product;
    public String serial;
    public int[] supportedIdentifierTypes;
    public VendorKeyValue[] vendorInfo;
    public String version;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.maker);
        parcel.writeString(this.product);
        parcel.writeString(this.version);
        parcel.writeString(this.serial);
        parcel.writeIntArray(this.supportedIdentifierTypes);
        parcel.writeTypedArray(this.vendorInfo, i);
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
                this.maker = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.product = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.version = parcel.readString();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.serial = parcel.readString();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.supportedIdentifierTypes = parcel.createIntArray();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.vendorInfo = (VendorKeyValue[]) parcel.createTypedArray(VendorKeyValue.CREATOR);
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

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("maker: " + Objects.toString(this.maker));
        stringJoiner.add("product: " + Objects.toString(this.product));
        stringJoiner.add("version: " + Objects.toString(this.version));
        stringJoiner.add("serial: " + Objects.toString(this.serial));
        stringJoiner.add("supportedIdentifierTypes: " + IdentifierType$$.arrayToString(this.supportedIdentifierTypes));
        stringJoiner.add("vendorInfo: " + Arrays.toString(this.vendorInfo));
        return "android.hardware.broadcastradio.Properties" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof Properties)) {
            Properties properties = (Properties) obj;
            return Objects.deepEquals(this.maker, properties.maker) && Objects.deepEquals(this.product, properties.product) && Objects.deepEquals(this.version, properties.version) && Objects.deepEquals(this.serial, properties.serial) && Objects.deepEquals(this.supportedIdentifierTypes, properties.supportedIdentifierTypes) && Objects.deepEquals(this.vendorInfo, properties.vendorInfo);
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.maker, this.product, this.version, this.serial, this.supportedIdentifierTypes, this.vendorInfo).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.vendorInfo) | 0;
    }

    public final int describeContents(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            int i = 0;
            for (Object obj2 : (Object[]) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }
}
