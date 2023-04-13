package android.media.session;

import android.media.session.MediaSession;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.KeyEvent;
/* loaded from: classes2.dex */
public interface IOnMediaKeyEventDispatchedListener extends IInterface {
    public static final String DESCRIPTOR = "android.media.session.IOnMediaKeyEventDispatchedListener";

    void onMediaKeyEventDispatched(KeyEvent keyEvent, String str, MediaSession.Token token) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IOnMediaKeyEventDispatchedListener {
        @Override // android.media.session.IOnMediaKeyEventDispatchedListener
        public void onMediaKeyEventDispatched(KeyEvent event, String packageName, MediaSession.Token sessionToken) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IOnMediaKeyEventDispatchedListener {
        static final int TRANSACTION_onMediaKeyEventDispatched = 1;

        public Stub() {
            attachInterface(this, IOnMediaKeyEventDispatchedListener.DESCRIPTOR);
        }

        public static IOnMediaKeyEventDispatchedListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOnMediaKeyEventDispatchedListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IOnMediaKeyEventDispatchedListener)) {
                return (IOnMediaKeyEventDispatchedListener) iin;
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
                    return "onMediaKeyEventDispatched";
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
                data.enforceInterface(IOnMediaKeyEventDispatchedListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOnMediaKeyEventDispatchedListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            KeyEvent _arg0 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            String _arg1 = data.readString();
                            MediaSession.Token _arg2 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaKeyEventDispatched(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IOnMediaKeyEventDispatchedListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOnMediaKeyEventDispatchedListener.DESCRIPTOR;
            }

            @Override // android.media.session.IOnMediaKeyEventDispatchedListener
            public void onMediaKeyEventDispatched(KeyEvent event, String packageName, MediaSession.Token sessionToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnMediaKeyEventDispatchedListener.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(sessionToken, 0);
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
