package android.view;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class VerifiedMotionEvent extends VerifiedInputEvent implements Parcelable {
    public static final Parcelable.Creator<VerifiedMotionEvent> CREATOR = new Parcelable.Creator<VerifiedMotionEvent>() { // from class: android.view.VerifiedMotionEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedMotionEvent[] newArray(int size) {
            return new VerifiedMotionEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedMotionEvent createFromParcel(Parcel in) {
            return new VerifiedMotionEvent(in);
        }
    };
    private static final String TAG = "VerifiedMotionEvent";
    private int mActionMasked;
    private int mButtonState;
    private long mDownTimeNanos;
    private int mFlags;
    private int mMetaState;
    private float mRawX;
    private float mRawY;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface MotionEventAction {
    }

    public Boolean getFlag(int flag) {
        switch (flag) {
            case 1:
            case 2:
            case 2048:
                return Boolean.valueOf((this.mFlags & flag) != 0);
            default:
                return null;
        }
    }

    public VerifiedMotionEvent(int deviceId, long eventTimeNanos, int source, int displayId, float rawX, float rawY, int actionMasked, long downTimeNanos, int flags, int metaState, int buttonState) {
        super(2, deviceId, eventTimeNanos, source, displayId);
        this.mRawX = rawX;
        this.mRawY = rawY;
        this.mActionMasked = actionMasked;
        AnnotationValidations.validate((Class<? extends Annotation>) MotionEventAction.class, (Annotation) null, actionMasked);
        this.mDownTimeNanos = downTimeNanos;
        this.mFlags = flags;
        this.mMetaState = metaState;
        this.mButtonState = buttonState;
    }

    public float getRawX() {
        return this.mRawX;
    }

    public float getRawY() {
        return this.mRawY;
    }

    public int getActionMasked() {
        return this.mActionMasked;
    }

    public long getDownTimeNanos() {
        return this.mDownTimeNanos;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getMetaState() {
        return this.mMetaState;
    }

    public int getButtonState() {
        return this.mButtonState;
    }

    @Override // android.view.VerifiedInputEvent
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerifiedMotionEvent that = (VerifiedMotionEvent) o;
        if (super.equals(that) && this.mRawX == that.mRawX && this.mRawY == that.mRawY && this.mActionMasked == that.mActionMasked && this.mDownTimeNanos == that.mDownTimeNanos && this.mFlags == that.mFlags && this.mMetaState == that.mMetaState && this.mButtonState == that.mButtonState) {
            return true;
        }
        return false;
    }

    @Override // android.view.VerifiedInputEvent
    public int hashCode() {
        int _hash = (1 * 31) + super.hashCode();
        return (((((((((((((_hash * 31) + Float.hashCode(this.mRawX)) * 31) + Float.hashCode(this.mRawY)) * 31) + this.mActionMasked) * 31) + Long.hashCode(this.mDownTimeNanos)) * 31) + this.mFlags) * 31) + this.mMetaState) * 31) + this.mButtonState;
    }

    @Override // android.view.VerifiedInputEvent, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.mRawX);
        dest.writeFloat(this.mRawY);
        dest.writeInt(this.mActionMasked);
        dest.writeLong(this.mDownTimeNanos);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mMetaState);
        dest.writeInt(this.mButtonState);
    }

    @Override // android.view.VerifiedInputEvent, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    VerifiedMotionEvent(Parcel in) {
        super(in, 2);
        float rawX = in.readFloat();
        float rawY = in.readFloat();
        int actionMasked = in.readInt();
        long downTimeNanos = in.readLong();
        int flags = in.readInt();
        int metaState = in.readInt();
        int buttonState = in.readInt();
        this.mRawX = rawX;
        this.mRawY = rawY;
        this.mActionMasked = actionMasked;
        AnnotationValidations.validate((Class<? extends Annotation>) MotionEventAction.class, (Annotation) null, actionMasked);
        this.mDownTimeNanos = downTimeNanos;
        this.mFlags = flags;
        this.mMetaState = metaState;
        this.mButtonState = buttonState;
    }

    @Deprecated
    private void __metadata() {
    }
}
