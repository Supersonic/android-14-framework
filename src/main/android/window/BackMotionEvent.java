package android.window;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.RemoteAnimationTarget;
/* loaded from: classes4.dex */
public final class BackMotionEvent implements Parcelable {
    public static final Parcelable.Creator<BackMotionEvent> CREATOR = new Parcelable.Creator<BackMotionEvent>() { // from class: android.window.BackMotionEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackMotionEvent createFromParcel(Parcel in) {
            return new BackMotionEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackMotionEvent[] newArray(int size) {
            return new BackMotionEvent[size];
        }
    };
    private final RemoteAnimationTarget mDepartingAnimationTarget;
    private final float mProgress;
    private final int mSwipeEdge;
    private final float mTouchX;
    private final float mTouchY;

    public BackMotionEvent(float touchX, float touchY, float progress, int swipeEdge, RemoteAnimationTarget departingAnimationTarget) {
        this.mTouchX = touchX;
        this.mTouchY = touchY;
        this.mProgress = progress;
        this.mSwipeEdge = swipeEdge;
        this.mDepartingAnimationTarget = departingAnimationTarget;
    }

    private BackMotionEvent(Parcel in) {
        this.mTouchX = in.readFloat();
        this.mTouchY = in.readFloat();
        this.mProgress = in.readFloat();
        this.mSwipeEdge = in.readInt();
        this.mDepartingAnimationTarget = (RemoteAnimationTarget) in.readTypedObject(RemoteAnimationTarget.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mTouchX);
        dest.writeFloat(this.mTouchY);
        dest.writeFloat(this.mProgress);
        dest.writeInt(this.mSwipeEdge);
        dest.writeTypedObject(this.mDepartingAnimationTarget, flags);
    }

    public float getProgress() {
        return this.mProgress;
    }

    public float getTouchX() {
        return this.mTouchX;
    }

    public float getTouchY() {
        return this.mTouchY;
    }

    public int getSwipeEdge() {
        return this.mSwipeEdge;
    }

    public RemoteAnimationTarget getDepartingAnimationTarget() {
        return this.mDepartingAnimationTarget;
    }

    public String toString() {
        return "BackMotionEvent{mTouchX=" + this.mTouchX + ", mTouchY=" + this.mTouchY + ", mProgress=" + this.mProgress + ", mSwipeEdge" + this.mSwipeEdge + ", mDepartingAnimationTarget" + this.mDepartingAnimationTarget + "}";
    }
}
