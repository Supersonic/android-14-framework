package android.view;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public abstract class VerifiedInputEvent implements Parcelable {
    public static final Parcelable.Creator<VerifiedInputEvent> CREATOR = new Parcelable.Creator<VerifiedInputEvent>() { // from class: android.view.VerifiedInputEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedInputEvent[] newArray(int size) {
            return new VerifiedInputEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedInputEvent createFromParcel(Parcel in) {
            int type = VerifiedInputEvent.peekInt(in);
            if (type == 1) {
                return VerifiedKeyEvent.CREATOR.createFromParcel(in);
            }
            if (type == 2) {
                return VerifiedMotionEvent.CREATOR.createFromParcel(in);
            }
            throw new IllegalArgumentException("Unexpected input event type in parcel.");
        }
    };
    private static final String TAG = "VerifiedInputEvent";
    protected static final int VERIFIED_KEY = 1;
    protected static final int VERIFIED_MOTION = 2;
    private int mDeviceId;
    private int mDisplayId;
    private long mEventTimeNanos;
    private int mSource;
    private int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface VerifiedInputEventType {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VerifiedInputEvent(int type, int deviceId, long eventTimeNanos, int source, int displayId) {
        this.mType = type;
        this.mDeviceId = deviceId;
        this.mEventTimeNanos = eventTimeNanos;
        this.mSource = source;
        this.mDisplayId = displayId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VerifiedInputEvent(Parcel in, int expectedType) {
        int readInt = in.readInt();
        this.mType = readInt;
        if (readInt != expectedType) {
            throw new IllegalArgumentException("Unexpected input event type token in parcel.");
        }
        this.mDeviceId = in.readInt();
        this.mEventTimeNanos = in.readLong();
        this.mSource = in.readInt();
        this.mDisplayId = in.readInt();
    }

    public int getDeviceId() {
        return this.mDeviceId;
    }

    public long getEventTimeNanos() {
        return this.mEventTimeNanos;
    }

    public int getSource() {
        return this.mSource;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeInt(this.mDeviceId);
        dest.writeLong(this.mEventTimeNanos);
        dest.writeInt(this.mSource);
        dest.writeInt(this.mDisplayId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int peekInt(Parcel parcel) {
        int initialDataPosition = parcel.dataPosition();
        int data = parcel.readInt();
        parcel.setDataPosition(initialDataPosition);
        return data;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerifiedInputEvent that = (VerifiedInputEvent) o;
        if (this.mType == that.mType && getDeviceId() == that.getDeviceId() && getEventTimeNanos() == that.getEventTimeNanos() && getSource() == that.getSource() && getDisplayId() == that.getDisplayId()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mType;
        return (((((((_hash * 31) + getDeviceId()) * 31) + Long.hashCode(getEventTimeNanos())) * 31) + getSource()) * 31) + getDisplayId();
    }
}
