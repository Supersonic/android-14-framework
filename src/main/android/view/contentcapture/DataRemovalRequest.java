package android.view.contentcapture;

import android.app.ActivityThread;
import android.content.LocusId;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.IntArray;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class DataRemovalRequest implements Parcelable {
    public static final Parcelable.Creator<DataRemovalRequest> CREATOR = new Parcelable.Creator<DataRemovalRequest>() { // from class: android.view.contentcapture.DataRemovalRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataRemovalRequest createFromParcel(Parcel parcel) {
            return new DataRemovalRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataRemovalRequest[] newArray(int size) {
            return new DataRemovalRequest[size];
        }
    };
    public static final int FLAG_IS_PREFIX = 1;
    private final boolean mForEverything;
    private ArrayList<LocusIdRequest> mLocusIdRequests;
    private final String mPackageName;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    @interface Flags {
    }

    private DataRemovalRequest(Builder builder) {
        this.mPackageName = ActivityThread.currentActivityThread().getApplication().getPackageName();
        this.mForEverything = builder.mForEverything;
        if (builder.mLocusIds != null) {
            int size = builder.mLocusIds.size();
            this.mLocusIdRequests = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                this.mLocusIdRequests.add(new LocusIdRequest((LocusId) builder.mLocusIds.get(i), builder.mFlags.get(i)));
            }
        }
    }

    private DataRemovalRequest(Parcel parcel) {
        this.mPackageName = parcel.readString();
        boolean readBoolean = parcel.readBoolean();
        this.mForEverything = readBoolean;
        if (!readBoolean) {
            int size = parcel.readInt();
            this.mLocusIdRequests = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                this.mLocusIdRequests.add(new LocusIdRequest((LocusId) parcel.readValue(null), parcel.readInt()));
            }
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isForEverything() {
        return this.mForEverything;
    }

    public List<LocusIdRequest> getLocusIdRequests() {
        return this.mLocusIdRequests;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private boolean mDestroyed;
        private IntArray mFlags;
        private boolean mForEverything;
        private ArrayList<LocusId> mLocusIds;

        public Builder forEverything() {
            throwIfDestroyed();
            Preconditions.checkState(this.mLocusIds == null, "Already added LocusIds");
            this.mForEverything = true;
            return this;
        }

        public Builder addLocusId(LocusId locusId, int flags) {
            throwIfDestroyed();
            Preconditions.checkState(!this.mForEverything, "Already is for everything");
            Objects.requireNonNull(locusId);
            if (this.mLocusIds == null) {
                this.mLocusIds = new ArrayList<>();
                this.mFlags = new IntArray();
            }
            this.mLocusIds.add(locusId);
            this.mFlags.add(flags);
            return this;
        }

        public DataRemovalRequest build() {
            throwIfDestroyed();
            Preconditions.checkState(this.mForEverything || this.mLocusIds != null, "must call either #forEverything() or add one #addLocusId()");
            this.mDestroyed = true;
            return new DataRemovalRequest(this);
        }

        private void throwIfDestroyed() {
            Preconditions.checkState(!this.mDestroyed, "Already destroyed!");
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mPackageName);
        parcel.writeBoolean(this.mForEverything);
        if (!this.mForEverything) {
            int size = this.mLocusIdRequests.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                LocusIdRequest request = this.mLocusIdRequests.get(i);
                parcel.writeValue(request.getLocusId());
                parcel.writeInt(request.getFlags());
            }
        }
    }

    /* loaded from: classes4.dex */
    public final class LocusIdRequest {
        private final int mFlags;
        private final LocusId mLocusId;

        private LocusIdRequest(LocusId locusId, int flags) {
            this.mLocusId = locusId;
            this.mFlags = flags;
        }

        public LocusId getLocusId() {
            return this.mLocusId;
        }

        public int getFlags() {
            return this.mFlags;
        }
    }
}
