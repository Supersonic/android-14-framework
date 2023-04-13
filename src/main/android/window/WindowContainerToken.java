package android.window;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.window.IWindowContainerToken;
/* loaded from: classes4.dex */
public final class WindowContainerToken implements Parcelable {
    public static final Parcelable.Creator<WindowContainerToken> CREATOR = new Parcelable.Creator<WindowContainerToken>() { // from class: android.window.WindowContainerToken.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowContainerToken createFromParcel(Parcel in) {
            return new WindowContainerToken(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowContainerToken[] newArray(int size) {
            return new WindowContainerToken[size];
        }
    };
    private final IWindowContainerToken mRealToken;

    public WindowContainerToken(IWindowContainerToken realToken) {
        this.mRealToken = realToken;
    }

    private WindowContainerToken(Parcel in) {
        this.mRealToken = IWindowContainerToken.Stub.asInterface(in.readStrongBinder());
    }

    public IBinder asBinder() {
        return this.mRealToken.asBinder();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mRealToken.asBinder());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        return this.mRealToken.asBinder().hashCode();
    }

    public String toString() {
        return "WCT{" + this.mRealToken + "}";
    }

    public boolean equals(Object obj) {
        return (obj instanceof WindowContainerToken) && this.mRealToken.asBinder() == ((WindowContainerToken) obj).asBinder();
    }
}
