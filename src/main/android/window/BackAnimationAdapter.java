package android.window;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.window.IBackAnimationRunner;
/* loaded from: classes4.dex */
public class BackAnimationAdapter implements Parcelable {
    public static final Parcelable.Creator<BackAnimationAdapter> CREATOR = new Parcelable.Creator<BackAnimationAdapter>() { // from class: android.window.BackAnimationAdapter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackAnimationAdapter createFromParcel(Parcel in) {
            return new BackAnimationAdapter(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackAnimationAdapter[] newArray(int size) {
            return new BackAnimationAdapter[size];
        }
    };
    private final IBackAnimationRunner mRunner;

    public BackAnimationAdapter(IBackAnimationRunner runner) {
        this.mRunner = runner;
    }

    public BackAnimationAdapter(Parcel in) {
        this.mRunner = IBackAnimationRunner.Stub.asInterface(in.readStrongBinder());
    }

    public IBackAnimationRunner getRunner() {
        return this.mRunner;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongInterface(this.mRunner);
    }
}
