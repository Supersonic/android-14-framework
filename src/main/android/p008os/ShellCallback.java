package android.p008os;

import android.p008os.Parcelable;
import android.util.Log;
import com.android.internal.p028os.IShellCallback;
/* renamed from: android.os.ShellCallback */
/* loaded from: classes3.dex */
public class ShellCallback implements Parcelable {
    public static final Parcelable.Creator<ShellCallback> CREATOR = new Parcelable.Creator<ShellCallback>() { // from class: android.os.ShellCallback.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShellCallback createFromParcel(Parcel in) {
            return new ShellCallback(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShellCallback[] newArray(int size) {
            return new ShellCallback[size];
        }
    };
    static final boolean DEBUG = false;
    static final String TAG = "ShellCallback";
    final boolean mLocal = true;
    IShellCallback mShellCallback;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.ShellCallback$MyShellCallback */
    /* loaded from: classes3.dex */
    public class MyShellCallback extends IShellCallback.Stub {
        MyShellCallback() {
        }

        @Override // com.android.internal.p028os.IShellCallback
        public ParcelFileDescriptor openFile(String path, String seLinuxContext, String mode) {
            return ShellCallback.this.onOpenFile(path, seLinuxContext, mode);
        }
    }

    public ShellCallback() {
    }

    public ParcelFileDescriptor openFile(String path, String seLinuxContext, String mode) {
        if (this.mLocal) {
            return onOpenFile(path, seLinuxContext, mode);
        }
        IShellCallback iShellCallback = this.mShellCallback;
        if (iShellCallback != null) {
            try {
                return iShellCallback.openFile(path, seLinuxContext, mode);
            } catch (RemoteException e) {
                Log.m103w(TAG, "Failure opening " + path, e);
                return null;
            }
        }
        return null;
    }

    public ParcelFileDescriptor onOpenFile(String path, String seLinuxContext, String mode) {
        return null;
    }

    public static void writeToParcel(ShellCallback callback, Parcel out) {
        if (callback == null) {
            out.writeStrongBinder(null);
        } else {
            callback.writeToParcel(out, 0);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        synchronized (this) {
            if (this.mShellCallback == null) {
                this.mShellCallback = new MyShellCallback();
            }
            out.writeStrongBinder(this.mShellCallback.asBinder());
        }
    }

    public IBinder getShellCallbackBinder() {
        return this.mShellCallback.asBinder();
    }

    ShellCallback(Parcel in) {
        IShellCallback asInterface = IShellCallback.Stub.asInterface(in.readStrongBinder());
        this.mShellCallback = asInterface;
        if (asInterface != null) {
            Binder.allowBlocking(asInterface.asBinder());
        }
    }
}
