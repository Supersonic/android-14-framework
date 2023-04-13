package android.hardware.hdmi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IHdmiControlStatusChangeListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiControlStatusChangeListener";

    void onStatusChange(int i, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IHdmiControlStatusChangeListener {
        @Override // android.hardware.hdmi.IHdmiControlStatusChangeListener
        public void onStatusChange(int isCecEnabled, boolean isCecAvailable) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IHdmiControlStatusChangeListener {
        static final int TRANSACTION_onStatusChange = 1;

        public Stub() {
            attachInterface(this, IHdmiControlStatusChangeListener.DESCRIPTOR);
        }

        public static IHdmiControlStatusChangeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IHdmiControlStatusChangeListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IHdmiControlStatusChangeListener)) {
                return (IHdmiControlStatusChangeListener) iin;
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
                    return "onStatusChange";
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
                data.enforceInterface(IHdmiControlStatusChangeListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IHdmiControlStatusChangeListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onStatusChange(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IHdmiControlStatusChangeListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IHdmiControlStatusChangeListener.DESCRIPTOR;
            }

            @Override // android.hardware.hdmi.IHdmiControlStatusChangeListener
            public void onStatusChange(int isCecEnabled, boolean isCecAvailable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IHdmiControlStatusChangeListener.DESCRIPTOR);
                    _data.writeInt(isCecEnabled);
                    _data.writeBoolean(isCecAvailable);
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
