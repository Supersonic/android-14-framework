package android.media.midi;

import android.media.midi.IMidiDeviceServer;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMidiDeviceOpenCallback extends IInterface {
    void onDeviceOpened(IMidiDeviceServer iMidiDeviceServer, IBinder iBinder) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMidiDeviceOpenCallback {
        @Override // android.media.midi.IMidiDeviceOpenCallback
        public void onDeviceOpened(IMidiDeviceServer server, IBinder token) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMidiDeviceOpenCallback {
        public static final String DESCRIPTOR = "android.media.midi.IMidiDeviceOpenCallback";
        static final int TRANSACTION_onDeviceOpened = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMidiDeviceOpenCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMidiDeviceOpenCallback)) {
                return (IMidiDeviceOpenCallback) iin;
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
                    return "onDeviceOpened";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IMidiDeviceServer _arg0 = IMidiDeviceServer.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onDeviceOpened(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMidiDeviceOpenCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.midi.IMidiDeviceOpenCallback
            public void onDeviceOpened(IMidiDeviceServer server, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(server);
                    _data.writeStrongBinder(token);
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
