package android.view;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class VerifiedKeyEvent extends VerifiedInputEvent implements Parcelable {
    public static final Parcelable.Creator<VerifiedKeyEvent> CREATOR = new Parcelable.Creator<VerifiedKeyEvent>() { // from class: android.view.VerifiedKeyEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedKeyEvent[] newArray(int size) {
            return new VerifiedKeyEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedKeyEvent createFromParcel(Parcel in) {
            return new VerifiedKeyEvent(in);
        }
    };
    private static final String TAG = "VerifiedKeyEvent";
    private int mAction;
    private long mDownTimeNanos;
    private int mFlags;
    private int mKeyCode;
    private int mMetaState;
    private int mRepeatCount;
    private int mScanCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface KeyEventAction {
    }

    public Boolean getFlag(int flag) {
        switch (flag) {
            case 32:
            case 2048:
                return Boolean.valueOf((this.mFlags & flag) != 0);
            default:
                return null;
        }
    }

    public VerifiedKeyEvent(int deviceId, long eventTimeNanos, int source, int displayId, int action, long downTimeNanos, int flags, int keyCode, int scanCode, int metaState, int repeatCount) {
        super(1, deviceId, eventTimeNanos, source, displayId);
        this.mAction = action;
        AnnotationValidations.validate((Class<? extends Annotation>) KeyEventAction.class, (Annotation) null, action);
        this.mDownTimeNanos = downTimeNanos;
        this.mFlags = flags;
        this.mKeyCode = keyCode;
        this.mScanCode = scanCode;
        this.mMetaState = metaState;
        this.mRepeatCount = repeatCount;
    }

    public int getAction() {
        return this.mAction;
    }

    public long getDownTimeNanos() {
        return this.mDownTimeNanos;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getKeyCode() {
        return this.mKeyCode;
    }

    public int getScanCode() {
        return this.mScanCode;
    }

    public int getMetaState() {
        return this.mMetaState;
    }

    public int getRepeatCount() {
        return this.mRepeatCount;
    }

    @Override // android.view.VerifiedInputEvent
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerifiedKeyEvent that = (VerifiedKeyEvent) o;
        if (super.equals(that) && this.mAction == that.mAction && this.mDownTimeNanos == that.mDownTimeNanos && this.mFlags == that.mFlags && this.mKeyCode == that.mKeyCode && this.mScanCode == that.mScanCode && this.mMetaState == that.mMetaState && this.mRepeatCount == that.mRepeatCount) {
            return true;
        }
        return false;
    }

    @Override // android.view.VerifiedInputEvent
    public int hashCode() {
        int _hash = (1 * 31) + super.hashCode();
        return (((((((((((((_hash * 31) + this.mAction) * 31) + Long.hashCode(this.mDownTimeNanos)) * 31) + this.mFlags) * 31) + this.mKeyCode) * 31) + this.mScanCode) * 31) + this.mMetaState) * 31) + this.mRepeatCount;
    }

    @Override // android.view.VerifiedInputEvent, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mAction);
        dest.writeLong(this.mDownTimeNanos);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mKeyCode);
        dest.writeInt(this.mScanCode);
        dest.writeInt(this.mMetaState);
        dest.writeInt(this.mRepeatCount);
    }

    @Override // android.view.VerifiedInputEvent, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    VerifiedKeyEvent(Parcel in) {
        super(in, 1);
        int action = in.readInt();
        long downTimeNanos = in.readLong();
        int flags = in.readInt();
        int keyCode = in.readInt();
        int scanCode = in.readInt();
        int metaState = in.readInt();
        int repeatCount = in.readInt();
        this.mAction = action;
        AnnotationValidations.validate((Class<? extends Annotation>) KeyEventAction.class, (Annotation) null, action);
        this.mDownTimeNanos = downTimeNanos;
        this.mFlags = flags;
        this.mKeyCode = keyCode;
        this.mScanCode = scanCode;
        this.mMetaState = metaState;
        this.mRepeatCount = repeatCount;
    }

    @Deprecated
    private void __metadata() {
    }
}
