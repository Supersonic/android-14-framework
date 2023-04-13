package android.window;

import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class ClientWindowFrames implements Parcelable {
    public static final Parcelable.Creator<ClientWindowFrames> CREATOR = new Parcelable.Creator<ClientWindowFrames>() { // from class: android.window.ClientWindowFrames.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClientWindowFrames createFromParcel(Parcel in) {
            return new ClientWindowFrames(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClientWindowFrames[] newArray(int size) {
            return new ClientWindowFrames[size];
        }
    };
    public Rect attachedFrame;
    public float compatScale;
    public final Rect displayFrame;
    public final Rect frame;
    public boolean isParentFrameClippedByDisplayCutout;
    public final Rect parentFrame;

    public ClientWindowFrames() {
        this.frame = new Rect();
        this.displayFrame = new Rect();
        this.parentFrame = new Rect();
        this.compatScale = 1.0f;
    }

    public ClientWindowFrames(ClientWindowFrames other) {
        Rect rect = new Rect();
        this.frame = rect;
        Rect rect2 = new Rect();
        this.displayFrame = rect2;
        Rect rect3 = new Rect();
        this.parentFrame = rect3;
        this.compatScale = 1.0f;
        rect.set(other.frame);
        rect2.set(other.displayFrame);
        rect3.set(other.parentFrame);
        if (other.attachedFrame != null) {
            this.attachedFrame = new Rect(other.attachedFrame);
        }
        this.isParentFrameClippedByDisplayCutout = other.isParentFrameClippedByDisplayCutout;
        this.compatScale = other.compatScale;
    }

    private ClientWindowFrames(Parcel in) {
        this.frame = new Rect();
        this.displayFrame = new Rect();
        this.parentFrame = new Rect();
        this.compatScale = 1.0f;
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.frame.readFromParcel(in);
        this.displayFrame.readFromParcel(in);
        this.parentFrame.readFromParcel(in);
        this.attachedFrame = (Rect) in.readTypedObject(Rect.CREATOR);
        this.isParentFrameClippedByDisplayCutout = in.readBoolean();
        this.compatScale = in.readFloat();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.frame.writeToParcel(dest, flags);
        this.displayFrame.writeToParcel(dest, flags);
        this.parentFrame.writeToParcel(dest, flags);
        dest.writeTypedObject(this.attachedFrame, flags);
        dest.writeBoolean(this.isParentFrameClippedByDisplayCutout);
        dest.writeFloat(this.compatScale);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        return "ClientWindowFrames{frame=" + this.frame.toShortString(sb) + " display=" + this.displayFrame.toShortString(sb) + " parentFrame=" + this.parentFrame.toShortString(sb) + (this.attachedFrame != null ? " attachedFrame=" + this.attachedFrame.toShortString() : "") + (this.isParentFrameClippedByDisplayCutout ? " parentClippedByDisplayCutout" : "") + (this.compatScale != 1.0f ? " sizeCompatScale=" + this.compatScale : "") + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
