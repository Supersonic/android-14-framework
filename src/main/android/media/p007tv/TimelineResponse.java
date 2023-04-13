package android.media.p007tv;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.TimelineResponse */
/* loaded from: classes2.dex */
public final class TimelineResponse extends BroadcastInfoResponse implements Parcelable {
    public static final Parcelable.Creator<TimelineResponse> CREATOR = new Parcelable.Creator<TimelineResponse>() { // from class: android.media.tv.TimelineResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimelineResponse createFromParcel(Parcel source) {
            source.readInt();
            return TimelineResponse.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimelineResponse[] newArray(int size) {
            return new TimelineResponse[size];
        }
    };
    private static final int RESPONSE_TYPE = 8;
    private final String mSelector;
    private final long mTicks;
    private final int mUnitsPerSecond;
    private final int mUnitsPerTick;
    private final long mWallClock;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TimelineResponse createFromParcelBody(Parcel in) {
        return new TimelineResponse(in);
    }

    public TimelineResponse(int requestId, int sequence, int responseResult, String selector, int unitsPerTick, int unitsPerSecond, long wallClock, long ticks) {
        super(8, requestId, sequence, responseResult);
        this.mSelector = selector;
        this.mUnitsPerTick = unitsPerTick;
        this.mUnitsPerSecond = unitsPerSecond;
        this.mWallClock = wallClock;
        this.mTicks = ticks;
    }

    TimelineResponse(Parcel source) {
        super(8, source);
        this.mSelector = source.readString();
        this.mUnitsPerTick = source.readInt();
        this.mUnitsPerSecond = source.readInt();
        this.mWallClock = source.readLong();
        this.mTicks = source.readLong();
    }

    public Uri getSelector() {
        return Uri.parse(this.mSelector);
    }

    public int getUnitsPerTick() {
        return this.mUnitsPerTick;
    }

    public int getUnitsPerSecond() {
        return this.mUnitsPerSecond;
    }

    public long getWallClock() {
        return this.mWallClock;
    }

    public long getTicks() {
        return this.mTicks;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mSelector);
        dest.writeInt(this.mUnitsPerTick);
        dest.writeInt(this.mUnitsPerSecond);
        dest.writeLong(this.mWallClock);
        dest.writeLong(this.mTicks);
    }
}
