package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.TimelineRequest */
/* loaded from: classes2.dex */
public final class TimelineRequest extends BroadcastInfoRequest implements Parcelable {
    public static final Parcelable.Creator<TimelineRequest> CREATOR = new Parcelable.Creator<TimelineRequest>() { // from class: android.media.tv.TimelineRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimelineRequest createFromParcel(Parcel source) {
            source.readInt();
            return TimelineRequest.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimelineRequest[] newArray(int size) {
            return new TimelineRequest[size];
        }
    };
    private static final int REQUEST_TYPE = 8;
    private final int mIntervalMillis;
    private final String mSelector;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TimelineRequest createFromParcelBody(Parcel in) {
        return new TimelineRequest(in);
    }

    public TimelineRequest(int requestId, int option, int intervalMillis) {
        super(8, requestId, option);
        this.mIntervalMillis = intervalMillis;
        this.mSelector = null;
    }

    public TimelineRequest(int requestId, int option, int intervalMillis, String selector) {
        super(8, requestId, option);
        this.mIntervalMillis = intervalMillis;
        this.mSelector = selector;
    }

    TimelineRequest(Parcel source) {
        super(8, source);
        this.mIntervalMillis = source.readInt();
        this.mSelector = source.readString();
    }

    public int getIntervalMillis() {
        return this.mIntervalMillis;
    }

    public String getSelector() {
        return this.mSelector;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mIntervalMillis);
        dest.writeString(this.mSelector);
    }
}
