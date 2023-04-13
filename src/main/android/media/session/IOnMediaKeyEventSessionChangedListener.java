package android.media.session;

import android.media.session.MediaSession;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IOnMediaKeyEventSessionChangedListener extends IInterface {
    public static final String DESCRIPTOR = "android.media.session.IOnMediaKeyEventSessionChangedListener";

    void onMediaKeyEventSessionChanged(String str, MediaSession.Token token) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IOnMediaKeyEventSessionChangedListener {
        @Override // android.media.session.IOnMediaKeyEventSessionChangedListener
        public void onMediaKeyEventSessionChanged(String packageName, MediaSession.Token mediaKeyEventSessionToken) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IOnMediaKeyEventSessionChangedListener {
        static final int TRANSACTION_onMediaKeyEventSessionChanged = 1;

        public Stub() {
            attachInterface(this, IOnMediaKeyEventSessionChangedListener.DESCRIPTOR);
        }

        public static IOnMediaKeyEventSessionChangedListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOnMediaKeyEventSessionChangedListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IOnMediaKeyEventSessionChangedListener)) {
                return (IOnMediaKeyEventSessionChangedListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onMediaKeyEventSessionChanged";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IOnMediaKeyEventSessionChangedListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOnMediaKeyEventSessionChangedListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            MediaSession.Token _arg1 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaKeyEventSessionChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IOnMediaKeyEventSessionChangedListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOnMediaKeyEventSessionChangedListener.DESCRIPTOR;
            }

            @Override // android.media.session.IOnMediaKeyEventSessionChangedListener
            public void onMediaKeyEventSessionChanged(String packageName, MediaSession.Token mediaKeyEventSessionToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnMediaKeyEventSessionChangedListener.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(mediaKeyEventSessionToken, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
