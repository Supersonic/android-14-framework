package android.hardware.broadcastradio;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ProgramInfo implements Parcelable {
    public static final Parcelable.Creator<ProgramInfo> CREATOR = new Parcelable.Creator<ProgramInfo>() { // from class: android.hardware.broadcastradio.ProgramInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramInfo createFromParcel(Parcel parcel) {
            ProgramInfo programInfo = new ProgramInfo();
            programInfo.readFromParcel(parcel);
            return programInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgramInfo[] newArray(int i) {
            return new ProgramInfo[i];
        }
    };
    public ProgramIdentifier logicallyTunedTo;
    public Metadata[] metadata;
    public ProgramIdentifier physicallyTunedTo;
    public ProgramIdentifier[] relatedContent;
    public ProgramSelector selector;
    public VendorKeyValue[] vendorInfo;
    public int infoFlags = 0;
    public int signalQuality = 0;

    public final int getStability() {
        return 1;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeTypedObject(this.selector, i);
        parcel.writeTypedObject(this.logicallyTunedTo, i);
        parcel.writeTypedObject(this.physicallyTunedTo, i);
        parcel.writeTypedArray(this.relatedContent, i);
        parcel.writeInt(this.infoFlags);
        parcel.writeInt(this.signalQuality);
        parcel.writeTypedArray(this.metadata, i);
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
                this.selector = (ProgramSelector) parcel.readTypedObject(ProgramSelector.CREATOR);
                if (parcel.dataPosition() - dataPosition < readInt) {
                    Parcelable.Creator<ProgramIdentifier> creator = ProgramIdentifier.CREATOR;
                    this.logicallyTunedTo = (ProgramIdentifier) parcel.readTypedObject(creator);
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.physicallyTunedTo = (ProgramIdentifier) parcel.readTypedObject(creator);
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.relatedContent = (ProgramIdentifier[]) parcel.createTypedArray(creator);
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.infoFlags = parcel.readInt();
                                if (parcel.dataPosition() - dataPosition < readInt) {
                                    this.signalQuality = parcel.readInt();
                                    if (parcel.dataPosition() - dataPosition < readInt) {
                                        this.metadata = (Metadata[]) parcel.createTypedArray(Metadata.CREATOR);
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
        stringJoiner.add("selector: " + Objects.toString(this.selector));
        stringJoiner.add("logicallyTunedTo: " + Objects.toString(this.logicallyTunedTo));
        stringJoiner.add("physicallyTunedTo: " + Objects.toString(this.physicallyTunedTo));
        stringJoiner.add("relatedContent: " + Arrays.toString(this.relatedContent));
        stringJoiner.add("infoFlags: " + this.infoFlags);
        stringJoiner.add("signalQuality: " + this.signalQuality);
        stringJoiner.add("metadata: " + Arrays.toString(this.metadata));
        stringJoiner.add("vendorInfo: " + Arrays.toString(this.vendorInfo));
        return "android.hardware.broadcastradio.ProgramInfo" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ProgramInfo)) {
            ProgramInfo programInfo = (ProgramInfo) obj;
            return Objects.deepEquals(this.selector, programInfo.selector) && Objects.deepEquals(this.logicallyTunedTo, programInfo.logicallyTunedTo) && Objects.deepEquals(this.physicallyTunedTo, programInfo.physicallyTunedTo) && Objects.deepEquals(this.relatedContent, programInfo.relatedContent) && Objects.deepEquals(Integer.valueOf(this.infoFlags), Integer.valueOf(programInfo.infoFlags)) && Objects.deepEquals(Integer.valueOf(this.signalQuality), Integer.valueOf(programInfo.signalQuality)) && Objects.deepEquals(this.metadata, programInfo.metadata) && Objects.deepEquals(this.vendorInfo, programInfo.vendorInfo);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.selector, this.logicallyTunedTo, this.physicallyTunedTo, this.relatedContent, Integer.valueOf(this.infoFlags), Integer.valueOf(this.signalQuality), this.metadata, this.vendorInfo).toArray());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.vendorInfo) | describeContents(this.selector) | 0 | describeContents(this.logicallyTunedTo) | describeContents(this.physicallyTunedTo) | describeContents(this.relatedContent) | describeContents(this.metadata);
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
