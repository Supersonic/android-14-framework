package android.window;

import android.annotation.NonNull;
import android.app.IApplicationThread;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.window.IRemoteTransition;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes4.dex */
public class RemoteTransition implements Parcelable {
    public static final Parcelable.Creator<RemoteTransition> CREATOR = new Parcelable.Creator<RemoteTransition>() { // from class: android.window.RemoteTransition.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteTransition[] newArray(int size) {
            return new RemoteTransition[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteTransition createFromParcel(Parcel in) {
            return new RemoteTransition(in);
        }
    };
    private IApplicationThread mAppThread;
    private IRemoteTransition mRemoteTransition;

    public RemoteTransition(IRemoteTransition remoteTransition) {
        this(remoteTransition, null);
    }

    public IBinder asBinder() {
        return this.mRemoteTransition.asBinder();
    }

    public RemoteTransition(IRemoteTransition remoteTransition, IApplicationThread appThread) {
        this.mRemoteTransition = remoteTransition;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) remoteTransition);
        this.mAppThread = appThread;
    }

    public IRemoteTransition getRemoteTransition() {
        return this.mRemoteTransition;
    }

    public IApplicationThread getAppThread() {
        return this.mAppThread;
    }

    public RemoteTransition setRemoteTransition(IRemoteTransition value) {
        this.mRemoteTransition = value;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) value);
        return this;
    }

    public RemoteTransition setAppThread(IApplicationThread value) {
        this.mAppThread = value;
        return this;
    }

    public String toString() {
        return "RemoteTransition { remoteTransition = " + this.mRemoteTransition + ", appThread = " + this.mAppThread + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mAppThread != null ? (byte) (0 | 2) : (byte) 0;
        dest.writeByte(flg);
        dest.writeStrongInterface(this.mRemoteTransition);
        IApplicationThread iApplicationThread = this.mAppThread;
        if (iApplicationThread != null) {
            dest.writeStrongInterface(iApplicationThread);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected RemoteTransition(Parcel in) {
        byte flg = in.readByte();
        IRemoteTransition remoteTransition = IRemoteTransition.Stub.asInterface(in.readStrongBinder());
        IApplicationThread appThread = (flg & 2) == 0 ? null : IApplicationThread.Stub.asInterface(in.readStrongBinder());
        this.mRemoteTransition = remoteTransition;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) remoteTransition);
        this.mAppThread = appThread;
    }

    @Deprecated
    private void __metadata() {
    }
}
