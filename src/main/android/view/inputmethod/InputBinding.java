package android.view.inputmethod;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public final class InputBinding implements Parcelable {
    public static final Parcelable.Creator<InputBinding> CREATOR = new Parcelable.Creator<InputBinding>() { // from class: android.view.inputmethod.InputBinding.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputBinding createFromParcel(Parcel source) {
            return new InputBinding(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputBinding[] newArray(int size) {
            return new InputBinding[size];
        }
    };
    static final String TAG = "InputBinding";
    final InputConnection mConnection;
    final IBinder mConnectionToken;
    final int mPid;
    final int mUid;

    public InputBinding(InputConnection conn, IBinder connToken, int uid, int pid) {
        this.mConnection = conn;
        this.mConnectionToken = connToken;
        this.mUid = uid;
        this.mPid = pid;
    }

    public InputBinding(InputConnection conn, InputBinding binding) {
        this.mConnection = conn;
        this.mConnectionToken = binding.getConnectionToken();
        this.mUid = binding.getUid();
        this.mPid = binding.getPid();
    }

    InputBinding(Parcel source) {
        this.mConnection = null;
        this.mConnectionToken = source.readStrongBinder();
        this.mUid = source.readInt();
        this.mPid = source.readInt();
    }

    public InputConnection getConnection() {
        return this.mConnection;
    }

    public IBinder getConnectionToken() {
        return this.mConnectionToken;
    }

    public int getUid() {
        return this.mUid;
    }

    public int getPid() {
        return this.mPid;
    }

    public String toString() {
        return "InputBinding{" + this.mConnectionToken + " / uid " + this.mUid + " / pid " + this.mPid + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mConnectionToken);
        dest.writeInt(this.mUid);
        dest.writeInt(this.mPid);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
