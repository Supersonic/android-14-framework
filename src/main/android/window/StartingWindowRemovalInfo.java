package android.window;

import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public final class StartingWindowRemovalInfo implements Parcelable {
    public static final Parcelable.Creator<StartingWindowRemovalInfo> CREATOR = new Parcelable.Creator<StartingWindowRemovalInfo>() { // from class: android.window.StartingWindowRemovalInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartingWindowRemovalInfo createFromParcel(Parcel source) {
            return new StartingWindowRemovalInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartingWindowRemovalInfo[] newArray(int size) {
            return new StartingWindowRemovalInfo[size];
        }
    };
    public boolean deferRemoveForIme;
    public Rect mainFrame;
    public boolean playRevealAnimation;
    public boolean removeImmediately;
    public float roundedCornerRadius;
    public int taskId;
    public SurfaceControl windowAnimationLeash;
    public boolean windowlessSurface;

    public StartingWindowRemovalInfo() {
    }

    private StartingWindowRemovalInfo(Parcel source) {
        readFromParcel(source);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    void readFromParcel(Parcel source) {
        this.taskId = source.readInt();
        this.windowAnimationLeash = (SurfaceControl) source.readTypedObject(SurfaceControl.CREATOR);
        this.mainFrame = (Rect) source.readTypedObject(Rect.CREATOR);
        this.playRevealAnimation = source.readBoolean();
        this.deferRemoveForIme = source.readBoolean();
        this.roundedCornerRadius = source.readFloat();
        this.windowlessSurface = source.readBoolean();
        this.removeImmediately = source.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.taskId);
        dest.writeTypedObject(this.windowAnimationLeash, flags);
        dest.writeTypedObject(this.mainFrame, flags);
        dest.writeBoolean(this.playRevealAnimation);
        dest.writeBoolean(this.deferRemoveForIme);
        dest.writeFloat(this.roundedCornerRadius);
        dest.writeBoolean(this.windowlessSurface);
        dest.writeBoolean(this.removeImmediately);
    }

    public String toString() {
        return "StartingWindowRemovalInfo{taskId=" + this.taskId + " frame=" + this.mainFrame + " playRevealAnimation=" + this.playRevealAnimation + " roundedCornerRadius=" + this.roundedCornerRadius + " deferRemoveForIme=" + this.deferRemoveForIme + " windowlessSurface=" + this.windowlessSurface + " removeImmediately=" + this.removeImmediately + "}";
    }
}
