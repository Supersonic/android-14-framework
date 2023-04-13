package android.hardware.radio;

import android.annotation.SystemApi;
import android.hardware.radio.ProgramSelector;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
@SystemApi
/* loaded from: classes2.dex */
public final class ProgramSelector implements Parcelable {
    public static final Parcelable.Creator<ProgramSelector> CREATOR = new Parcelable.Creator<ProgramSelector>() { // from class: android.hardware.radio.ProgramSelector.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProgramSelector createFromParcel(Parcel in) {
            return new ProgramSelector(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProgramSelector[] newArray(int size) {
            return new ProgramSelector[size];
        }
    };
    public static final int IDENTIFIER_TYPE_AMFM_FREQUENCY = 1;
    public static final int IDENTIFIER_TYPE_DAB_DMB_SID_EXT = 14;
    public static final int IDENTIFIER_TYPE_DAB_ENSEMBLE = 6;
    public static final int IDENTIFIER_TYPE_DAB_FREQUENCY = 8;
    public static final int IDENTIFIER_TYPE_DAB_SCID = 7;
    @Deprecated
    public static final int IDENTIFIER_TYPE_DAB_SIDECC = 5;
    @Deprecated
    public static final int IDENTIFIER_TYPE_DAB_SID_EXT = 5;
    public static final int IDENTIFIER_TYPE_DRMO_FREQUENCY = 10;
    @Deprecated
    public static final int IDENTIFIER_TYPE_DRMO_MODULATION = 11;
    public static final int IDENTIFIER_TYPE_DRMO_SERVICE_ID = 9;
    public static final int IDENTIFIER_TYPE_HD_STATION_ID_EXT = 3;
    public static final int IDENTIFIER_TYPE_HD_STATION_NAME = 10004;
    @Deprecated
    public static final int IDENTIFIER_TYPE_HD_SUBCHANNEL = 4;
    public static final int IDENTIFIER_TYPE_INVALID = 0;
    public static final int IDENTIFIER_TYPE_RDS_PI = 2;
    public static final int IDENTIFIER_TYPE_SXM_CHANNEL = 13;
    public static final int IDENTIFIER_TYPE_SXM_SERVICE_ID = 12;
    public static final int IDENTIFIER_TYPE_VENDOR_END = 1999;
    @Deprecated
    public static final int IDENTIFIER_TYPE_VENDOR_PRIMARY_END = 1999;
    @Deprecated
    public static final int IDENTIFIER_TYPE_VENDOR_PRIMARY_START = 1000;
    public static final int IDENTIFIER_TYPE_VENDOR_START = 1000;
    @Deprecated
    public static final int PROGRAM_TYPE_AM = 1;
    @Deprecated
    public static final int PROGRAM_TYPE_AM_HD = 3;
    @Deprecated
    public static final int PROGRAM_TYPE_DAB = 5;
    @Deprecated
    public static final int PROGRAM_TYPE_DRMO = 6;
    @Deprecated
    public static final int PROGRAM_TYPE_FM = 2;
    @Deprecated
    public static final int PROGRAM_TYPE_FM_HD = 4;
    @Deprecated
    public static final int PROGRAM_TYPE_INVALID = 0;
    @Deprecated
    public static final int PROGRAM_TYPE_SXM = 7;
    @Deprecated
    public static final int PROGRAM_TYPE_VENDOR_END = 1999;
    @Deprecated
    public static final int PROGRAM_TYPE_VENDOR_START = 1000;
    private final Identifier mPrimaryId;
    private final int mProgramType;
    private final Identifier[] mSecondaryIds;
    private final long[] mVendorIds;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface IdentifierType {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Deprecated
    /* loaded from: classes2.dex */
    public @interface ProgramType {
    }

    public ProgramSelector(int programType, Identifier primaryId, Identifier[] secondaryIds, long[] vendorIds) {
        secondaryIds = secondaryIds == null ? new Identifier[0] : secondaryIds;
        vendorIds = vendorIds == null ? new long[0] : vendorIds;
        if (Stream.of((Object[]) secondaryIds).anyMatch(new Predicate() { // from class: android.hardware.radio.ProgramSelector$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ProgramSelector.lambda$new$0((ProgramSelector.Identifier) obj);
            }
        })) {
            throw new IllegalArgumentException("secondaryIds list must not contain nulls");
        }
        this.mProgramType = programType;
        this.mPrimaryId = (Identifier) Objects.requireNonNull(primaryId);
        this.mSecondaryIds = secondaryIds;
        this.mVendorIds = vendorIds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$0(Identifier id) {
        return id == null;
    }

    @Deprecated
    public int getProgramType() {
        return this.mProgramType;
    }

    public Identifier getPrimaryId() {
        return this.mPrimaryId;
    }

    public Identifier[] getSecondaryIds() {
        return this.mSecondaryIds;
    }

    public long getFirstId(int type) {
        Identifier[] identifierArr;
        if (this.mPrimaryId.getType() == type) {
            return this.mPrimaryId.getValue();
        }
        for (Identifier id : this.mSecondaryIds) {
            if (id.getType() == type) {
                return id.getValue();
            }
        }
        throw new IllegalArgumentException("Identifier " + type + " not found");
    }

    public Identifier[] getAllIds(int type) {
        Identifier[] identifierArr;
        List<Identifier> out = new ArrayList<>();
        if (this.mPrimaryId.getType() == type) {
            out.add(this.mPrimaryId);
        }
        for (Identifier id : this.mSecondaryIds) {
            if (id.getType() == type) {
                out.add(id);
            }
        }
        return (Identifier[]) out.toArray(new Identifier[out.size()]);
    }

    @Deprecated
    public long[] getVendorIds() {
        return this.mVendorIds;
    }

    public ProgramSelector withSecondaryPreferred(Identifier preferred) {
        final int preferredType = preferred.getType();
        Identifier[] secondaryIds = (Identifier[]) Stream.concat(Arrays.stream(this.mSecondaryIds).filter(new Predicate() { // from class: android.hardware.radio.ProgramSelector$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ProgramSelector.lambda$withSecondaryPreferred$1(preferredType, (ProgramSelector.Identifier) obj);
            }
        }), Stream.of(preferred)).toArray(new IntFunction() { // from class: android.hardware.radio.ProgramSelector$$ExternalSyntheticLambda2
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return ProgramSelector.lambda$withSecondaryPreferred$2(i);
            }
        });
        return new ProgramSelector(this.mProgramType, this.mPrimaryId, secondaryIds, this.mVendorIds);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$withSecondaryPreferred$1(int preferredType, Identifier id) {
        return id.getType() != preferredType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Identifier[] lambda$withSecondaryPreferred$2(int x$0) {
        return new Identifier[x$0];
    }

    public static ProgramSelector createAmFmSelector(int band, int frequencyKhz) {
        return createAmFmSelector(band, frequencyKhz, 0);
    }

    private static boolean isValidAmFmFrequency(boolean isAm, int frequencyKhz) {
        return isAm ? frequencyKhz > 150 && frequencyKhz <= 30000 : frequencyKhz > 60000 && frequencyKhz < 110000;
    }

    public static ProgramSelector createAmFmSelector(int band, int frequencyKhz, int subChannel) {
        boolean isDigital = false;
        if (band == -1) {
            if (frequencyKhz < 50000) {
                band = subChannel <= 0 ? 0 : 3;
            } else {
                band = subChannel <= 0 ? 1 : 2;
            }
        }
        boolean isAm = band == 0 || band == 3;
        if (band == 3 || band == 2) {
            isDigital = true;
        }
        if (!isAm && !isDigital && band != 1) {
            throw new IllegalArgumentException("Unknown band: " + band);
        }
        if (subChannel < 0 || subChannel > 8) {
            throw new IllegalArgumentException("Invalid subchannel: " + subChannel);
        }
        if (subChannel > 0 && !isDigital) {
            throw new IllegalArgumentException("Subchannels are not supported for non-HD radio");
        }
        if (!isValidAmFmFrequency(isAm, frequencyKhz)) {
            throw new IllegalArgumentException("Provided value is not a valid AM/FM frequency: " + frequencyKhz);
        }
        int programType = isAm ? 1 : 2;
        Identifier primary = new Identifier(1, frequencyKhz);
        Identifier[] secondary = null;
        if (subChannel > 0) {
            secondary = new Identifier[]{new Identifier(4, subChannel - 1)};
        }
        return new ProgramSelector(programType, primary, secondary, null);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ProgramSelector(type=").append(this.mProgramType).append(", primary=").append(this.mPrimaryId);
        if (this.mSecondaryIds.length > 0) {
            sb.append(", secondary=").append(Arrays.toString(this.mSecondaryIds));
        }
        if (this.mVendorIds.length > 0) {
            sb.append(", vendor=").append(Arrays.toString(this.mVendorIds));
        }
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }

    public int hashCode() {
        return this.mPrimaryId.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ProgramSelector) {
            ProgramSelector other = (ProgramSelector) obj;
            return this.mPrimaryId.equals(other.getPrimaryId());
        }
        return false;
    }

    public boolean strictEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ProgramSelector) {
            ProgramSelector other = (ProgramSelector) obj;
            if (this.mPrimaryId.equals(other.getPrimaryId())) {
                Identifier[] identifierArr = this.mSecondaryIds;
                if (identifierArr.length == other.mSecondaryIds.length && Arrays.asList(identifierArr).containsAll(Arrays.asList(other.mSecondaryIds))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private ProgramSelector(Parcel in) {
        this.mProgramType = in.readInt();
        this.mPrimaryId = (Identifier) in.readTypedObject(Identifier.CREATOR);
        Identifier[] identifierArr = (Identifier[]) in.createTypedArray(Identifier.CREATOR);
        this.mSecondaryIds = identifierArr;
        if (Stream.of((Object[]) identifierArr).anyMatch(new Predicate() { // from class: android.hardware.radio.ProgramSelector$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ProgramSelector.lambda$new$3((ProgramSelector.Identifier) obj);
            }
        })) {
            throw new IllegalArgumentException("secondaryIds list must not contain nulls");
        }
        this.mVendorIds = in.createLongArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$3(Identifier id) {
        return id == null;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mProgramType);
        dest.writeTypedObject(this.mPrimaryId, 0);
        dest.writeTypedArray(this.mSecondaryIds, 0);
        dest.writeLongArray(this.mVendorIds);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes2.dex */
    public static final class Identifier implements Parcelable {
        public static final Parcelable.Creator<Identifier> CREATOR = new Parcelable.Creator<Identifier>() { // from class: android.hardware.radio.ProgramSelector.Identifier.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Identifier createFromParcel(Parcel in) {
                return new Identifier(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Identifier[] newArray(int size) {
                return new Identifier[size];
            }
        };
        private final int mType;
        private final long mValue;

        public Identifier(int type, long value) {
            this.mType = type == 10004 ? 4 : type;
            this.mValue = value;
        }

        public int getType() {
            int i = this.mType;
            if (i == 4 && this.mValue > 10) {
                return 10004;
            }
            return i;
        }

        public boolean isCategoryType() {
            int i = this.mType;
            return (i >= 1000 && i <= 1999) || i == 6;
        }

        public long getValue() {
            return this.mValue;
        }

        public String toString() {
            return "Identifier(" + this.mType + ", " + this.mValue + NavigationBarInflaterView.KEY_CODE_END;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mType), Long.valueOf(this.mValue));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Identifier) {
                Identifier other = (Identifier) obj;
                return other.getType() == this.mType && other.getValue() == this.mValue;
            }
            return false;
        }

        private Identifier(Parcel in) {
            this.mType = in.readInt();
            this.mValue = in.readLong();
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mType);
            dest.writeLong(this.mValue);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }
}
