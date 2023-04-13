package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
@SystemApi
/* loaded from: classes2.dex */
public final class NearbyDevice implements Parcelable {
    public static final int RANGE_CLOSE = 3;
    public static final int RANGE_FAR = 1;
    public static final int RANGE_LONG = 2;
    public static final int RANGE_UNKNOWN = 0;
    public static final int RANGE_WITHIN_REACH = 4;
    private final String mMediaRoute2Id;
    private final int mRangeZone;
    private static final List<Integer> RANGE_WEIGHT_LIST = Arrays.asList(0, 1, 2, 3, 4);
    public static final Parcelable.Creator<NearbyDevice> CREATOR = new Parcelable.Creator<NearbyDevice>() { // from class: android.media.NearbyDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NearbyDevice createFromParcel(Parcel in) {
            return new NearbyDevice(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NearbyDevice[] newArray(int size) {
            return new NearbyDevice[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RangeZone {
    }

    public static String rangeZoneToString(int rangeZone) {
        switch (rangeZone) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "FAR";
            case 2:
                return "LONG";
            case 3:
                return "CLOSE";
            case 4:
                return "WITHIN_REACH";
            default:
                return "Invalid";
        }
    }

    public NearbyDevice(String mediaRoute2Id, int rangeZone) {
        this.mMediaRoute2Id = mediaRoute2Id;
        this.mRangeZone = rangeZone;
    }

    private NearbyDevice(Parcel in) {
        this.mMediaRoute2Id = in.readString8();
        this.mRangeZone = in.readInt();
    }

    public static int compareRangeZones(int rangeZone, int anotherRangeZone) {
        if (rangeZone == anotherRangeZone) {
            return 0;
        }
        List<Integer> list = RANGE_WEIGHT_LIST;
        return list.indexOf(Integer.valueOf(rangeZone)) > list.indexOf(Integer.valueOf(anotherRangeZone)) ? -1 : 1;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "NearbyDevice{mediaRoute2Id=" + this.mMediaRoute2Id + " rangeZone=" + rangeZoneToString(this.mRangeZone) + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mMediaRoute2Id);
        dest.writeInt(this.mRangeZone);
    }

    public String getMediaRoute2Id() {
        return this.mMediaRoute2Id;
    }

    public int getRangeZone() {
        return this.mRangeZone;
    }
}
