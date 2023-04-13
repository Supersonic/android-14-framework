package android.app.search;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SearchTargetEvent implements Parcelable {
    public static final int ACTION_DELETE = 9;
    public static final int ACTION_DISMISS = 10;
    public static final int ACTION_DRAGNDROP = 7;
    public static final int ACTION_LAUNCH_KEYBOARD_FOCUS = 6;
    public static final int ACTION_LAUNCH_TOUCH = 5;
    public static final int ACTION_LONGPRESS = 4;
    public static final int ACTION_SURFACE_INVISIBLE = 8;
    public static final int ACTION_SURFACE_VISIBLE = 1;
    public static final int ACTION_TAP = 3;
    public static final Parcelable.Creator<SearchTargetEvent> CREATOR = new Parcelable.Creator<SearchTargetEvent>() { // from class: android.app.search.SearchTargetEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchTargetEvent createFromParcel(Parcel parcel) {
            return new SearchTargetEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchTargetEvent[] newArray(int size) {
            return new SearchTargetEvent[size];
        }
    };
    public static final int FLAG_IME_SHOWN = 1;
    private final int mAction;
    private int mFlags;
    private final String mLocation;
    private final List<String> mTargetIds;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ActionType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FlagType {
    }

    private SearchTargetEvent(List<String> targetIds, String location, int actionType, int flags) {
        this.mTargetIds = (List) Objects.requireNonNull(targetIds);
        this.mLocation = location;
        this.mAction = actionType;
        this.mFlags = flags;
    }

    private SearchTargetEvent(Parcel parcel) {
        ArrayList arrayList = new ArrayList();
        this.mTargetIds = arrayList;
        parcel.readStringList(arrayList);
        this.mLocation = parcel.readString();
        this.mAction = parcel.readInt();
        this.mFlags = parcel.readInt();
    }

    public String getTargetId() {
        return this.mTargetIds.get(0);
    }

    public List<String> getTargetIds() {
        return this.mTargetIds;
    }

    public String getLaunchLocation() {
        return this.mLocation;
    }

    public int getAction() {
        return this.mAction;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int hashCode() {
        return this.mTargetIds.get(0).hashCode() + this.mAction;
    }

    public boolean equals(Object o) {
        if (getClass().equals(o != null ? o.getClass() : null)) {
            SearchTargetEvent other = (SearchTargetEvent) o;
            return (this.mTargetIds.equals(other.mTargetIds) && this.mAction == other.mAction && this.mFlags == other.mFlags && this.mLocation == null) ? other.mLocation == null : this.mLocation.equals(other.mLocation);
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mTargetIds);
        dest.writeString(this.mLocation);
        dest.writeInt(this.mAction);
        dest.writeInt(this.mFlags);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private int mAction;
        private int mFlags;
        private String mLocation;
        private List<String> mTargetIds;

        public Builder(String id, int actionType) {
            ArrayList arrayList = new ArrayList();
            this.mTargetIds = arrayList;
            arrayList.add(id);
            this.mAction = actionType;
        }

        public Builder(List<String> ids, int actionType) {
            this.mTargetIds = ids;
            this.mAction = actionType;
        }

        public Builder setLaunchLocation(String location) {
            this.mLocation = location;
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags = flags;
            return this;
        }

        public SearchTargetEvent build() {
            return new SearchTargetEvent(this.mTargetIds, this.mLocation, this.mAction, this.mFlags);
        }
    }
}
