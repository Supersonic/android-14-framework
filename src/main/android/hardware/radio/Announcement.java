package android.hardware.radio;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class Announcement implements Parcelable {
    public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() { // from class: android.hardware.radio.Announcement.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Announcement createFromParcel(Parcel in) {
            return new Announcement(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };
    public static final int TYPE_EMERGENCY = 1;
    public static final int TYPE_EVENT = 6;
    public static final int TYPE_MISC = 8;
    public static final int TYPE_NEWS = 5;
    public static final int TYPE_SPORT = 7;
    public static final int TYPE_TRAFFIC = 3;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_WEATHER = 4;
    private final ProgramSelector mSelector;
    private final int mType;
    private final Map<String, String> mVendorInfo;

    /* loaded from: classes2.dex */
    public interface OnListUpdatedListener {
        void onListUpdated(Collection<Announcement> collection);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    public Announcement(ProgramSelector selector, int type, Map<String, String> vendorInfo) {
        this.mSelector = (ProgramSelector) Objects.requireNonNull(selector, "Program selector cannot be null");
        this.mType = type;
        this.mVendorInfo = (Map) Objects.requireNonNull(vendorInfo, "Vendor info cannot be null");
    }

    private Announcement(Parcel in) {
        this.mSelector = (ProgramSelector) in.readTypedObject(ProgramSelector.CREATOR);
        this.mType = in.readInt();
        this.mVendorInfo = Utils.readStringMap(in);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mSelector, 0);
        dest.writeInt(this.mType);
        Utils.writeStringMap(dest, this.mVendorInfo);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ProgramSelector getSelector() {
        return this.mSelector;
    }

    public int getType() {
        return this.mType;
    }

    public Map<String, String> getVendorInfo() {
        return this.mVendorInfo;
    }
}
