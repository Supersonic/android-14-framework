package android.view;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.view.IInputMonitorHost;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes4.dex */
public final class InputMonitor implements Parcelable {
    public static final Parcelable.Creator<InputMonitor> CREATOR = new Parcelable.Creator<InputMonitor>() { // from class: android.view.InputMonitor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMonitor[] newArray(int size) {
            return new InputMonitor[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMonitor createFromParcel(Parcel in) {
            return new InputMonitor(in);
        }
    };
    private static final boolean DEBUG = false;
    private static final String TAG = "InputMonitor";
    private final IInputMonitorHost mHost;
    private final InputChannel mInputChannel;

    public void pilferPointers() {
        try {
            this.mHost.pilferPointers();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void dispose() {
        this.mInputChannel.dispose();
        try {
            this.mHost.dispose();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public InputMonitor(InputChannel inputChannel, IInputMonitorHost host) {
        this.mInputChannel = inputChannel;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) inputChannel);
        this.mHost = host;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) host);
    }

    public InputChannel getInputChannel() {
        return this.mInputChannel;
    }

    public IInputMonitorHost getHost() {
        return this.mHost;
    }

    public String toString() {
        return "InputMonitor { inputChannel = " + this.mInputChannel + ", host = " + this.mHost + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mInputChannel, flags);
        dest.writeStrongInterface(this.mHost);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InputMonitor(Parcel in) {
        InputChannel inputChannel = (InputChannel) in.readTypedObject(InputChannel.CREATOR);
        IInputMonitorHost host = IInputMonitorHost.Stub.asInterface(in.readStrongBinder());
        this.mInputChannel = inputChannel;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) inputChannel);
        this.mHost = host;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) host);
    }

    @Deprecated
    private void __metadata() {
    }
}
