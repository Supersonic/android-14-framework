package android.p008os;

import android.p008os.IMessenger;
import android.p008os.Parcelable;
/* renamed from: android.os.Messenger */
/* loaded from: classes3.dex */
public final class Messenger implements Parcelable {
    public static final Parcelable.Creator<Messenger> CREATOR = new Parcelable.Creator<Messenger>() { // from class: android.os.Messenger.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Messenger createFromParcel(Parcel in) {
            IBinder target = in.readStrongBinder();
            if (target != null) {
                return new Messenger(target);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Messenger[] newArray(int size) {
            return new Messenger[size];
        }
    };
    private final IMessenger mTarget;

    public Messenger(Handler target) {
        this.mTarget = target.getIMessenger();
    }

    public void send(Message message) throws RemoteException {
        this.mTarget.send(message);
    }

    public IBinder getBinder() {
        return this.mTarget.asBinder();
    }

    public boolean equals(Object otherObj) {
        if (otherObj == null) {
            return false;
        }
        try {
            return this.mTarget.asBinder().equals(((Messenger) otherObj).mTarget.asBinder());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.mTarget.asBinder().hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.mTarget.asBinder());
    }

    public static void writeMessengerOrNullToParcel(Messenger messenger, Parcel out) {
        out.writeStrongBinder(messenger != null ? messenger.mTarget.asBinder() : null);
    }

    public static Messenger readMessengerOrNullFromParcel(Parcel in) {
        IBinder b = in.readStrongBinder();
        if (b != null) {
            return new Messenger(b);
        }
        return null;
    }

    public Messenger(IBinder target) {
        this.mTarget = IMessenger.Stub.asInterface(target);
    }
}
